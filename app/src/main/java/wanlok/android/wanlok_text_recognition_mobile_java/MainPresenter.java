package wanlok.android.wanlok_text_recognition_mobile_java;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class MainPresenter extends NanoHTTPD {
    private static final String TAG = MainPresenter.class.getName();
    private Activity activity;
    private TextRecognizer textRecognizer;

    public MainPresenter(Activity activity) throws IOException {
        super(8082);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        this.activity = activity;
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    private JSONObject readBitmap(Bitmap bitmap) {
        JSONObject jsonObject = new JSONObject();
        if (bitmap != null) {
            InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
            textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text text) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(text.getText());
                    Utils.set(jsonObject, "texts", jsonArray);
                    Utils.set(jsonObject, "count", 1);
                    Utils.set(jsonObject, "status", "OK");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Utils.set(jsonObject, "status", "FAIL");
                }
            });
        } else {
            Utils.set(jsonObject, "status", "FAIL");
        }
        while (!jsonObject.has("status")) {

        }
        return jsonObject;
    }

    private JSONObject readPDF(File file) {
        JSONObject jsonObject = new JSONObject();
        try {
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//            int width = (int) (displayMetrics.widthPixels / displayMetrics.xdpi * 264);
            int width = displayMetrics.widthPixels;
            JSONArray jsonArray = new JSONArray();
            int pageCount = pdfRenderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = pdfRenderer.openPage(i);
                int height = page.getHeight() * width / page.getWidth();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                jsonArray.put(Utils.get(readBitmap(bitmap), "texts", 0));
            }
            pdfRenderer.close();
            Utils.set(jsonObject, "texts", jsonArray);
            Utils.set(jsonObject, "count", pageCount);
            Utils.set(jsonObject, "status", "OK");
        } catch (Exception e) {
            Utils.set(jsonObject, "status", "FAIL");
        }
        return jsonObject;
    }

    @Override
    public Response serve(IHTTPSession session) {
        JSONObject jsonObject = new JSONObject();
        try {
            Map<String, String> body = new HashMap<>();
            session.parseBody(body);
            String tempFilePath = body.get("file");
            // Download the file
            File file = new File(activity.getFilesDir() + File.separator + "temp");
            Utils.copy(new File(tempFilePath), file);
            // Read as image
            jsonObject = readBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            // Read as pdf if not able to read as image
            if (Utils.getString(jsonObject, "status").equals("FAIL")) {
                jsonObject = readPDF(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse(Response.Status.OK, "application/json", jsonObject.toString());
    }
}
