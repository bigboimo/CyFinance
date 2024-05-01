package com.example.cyfinance.util;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MultipartRequest extends Request<String> {
    private final Response.Listener<String> mListener; // Listener for the response
    private final byte[] mRequestBody; // Byte array that will hold the multipart data
    private static final String PROTOCOL_CHARSET = "utf-8"; // The character set for encoding the multipart data
    private static final String BOUNDARY = "application-" + System.currentTimeMillis(); // Boundary for the multipart data, unique
    private static final String LINE_END = "\r\n"; // Line end for multipart
    private static final String TWO_HYPHENS = "--"; // Two hyphens used to prefix boundaries

    /**
     * Constructor for the MultipartRequest.
     * @param method the HTTP method to use (GET, POST, PUT, etc.).
     * @param url the URL to which the request is sent.
     * @param requestBody the body of the request as byte array.
     * @param listener response listener.
     * @param errorListener error listener to handle request errors.
     */
    public MultipartRequest(int method, String url, byte[] requestBody, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mRequestBody = requestBody;
    }

    @Override
    public String getBodyContentType() {
        // Content type for a multipart request. Includes the boundary string.
        return "multipart/form-data;boundary=" + BOUNDARY;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        // Returns the prepared multipart data.
        return mRequestBody;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            // Parse the network response to a readable String using the defined character set.
            String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            // Create a successful response instance.
            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            // Return an error response if there is an issue parsing the response.
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        // Deliver the response to the registered listener.
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        // Deliver the error to the registered error listener.
        super.deliverError(error);
    }

    /**
     * Utility method to build the multipart body data.
     * @param key the form field name for the file part.
     * @param fileName the filename for the file part.
     * @param fileData the actual file data.
     * @param mimeType the MIME type of the file.
     * @return a byte array containing the multipart form data.
     * @throws IOException if an I/O error occurs during the writing of the data.
     */
    public static byte[] createMultipartRequestData(String key, String fileName, byte[] fileData, String mimeType) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        // Start of the multipart data. Includes the boundary and headers for the file part.
        dos.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"" + LINE_END);
        dos.writeBytes("Content-Type: " + mimeType + LINE_END);
        dos.writeBytes(LINE_END);

        // Writing the actual file data to the output stream.
        dos.write(fileData);
        dos.writeBytes(LINE_END);

        // End of the multipart data.
        dos.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);
        dos.close();

        return bos.toByteArray();
    }
}
