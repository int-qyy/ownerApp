package com.swufe.owner;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.swufe.owner.GetMP3;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.swufe.owner.GetMP3.request;

public class TestVo extends AppCompatActivity {

    private static final String TAG="TestVo";
    VoItem voItem = null;
    String english,chinese;
    EditText EnString;
    TextView ChString,ShowString;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_vo);
        EnString=(EditText)findViewById(R.id.ipt);
        ChString=(TextView)findViewById(R.id.ch1);
        ShowString=(TextView) findViewById(R.id.showVo);

        DBManager dbManager = new DBManager(TestVo.this);
        Random r = new Random();
        int ran1 = r.nextInt(100000);
        int len=dbManager.count();
        int ran=ran1%len;
        Log.i(TAG,"len:====="+len);
        Log.i(TAG,"now id===="+ran);
        voItem= dbManager.findById(ran);
        while(voItem==null){
            r = new Random();
             ran1 = r.nextInt(100000);
             ran=ran1%len;
            Log.i(TAG,"len:====="+len);
            Log.i(TAG,"now id===="+ran);
            voItem= dbManager.findById(ran);
        }
        english=voItem.getEnString();
        Log.i(TAG,"english=="+english);
        chinese=voItem.getChString();
        Log.i(TAG,"chinese==="+chinese);
        ChString.setText(chinese);


        String httpUrl = "https://apis.baidu.com/heweather/weather/free";
        String httpArg = "city=wuhan";
        String jsonResult = GetMP3.request(httpUrl, httpArg);
        /**
        JSONObject obj = JSONObject.fromObject(jsonResult);
        String result = obj.getString("HeWeather data service 3.0");
        JSONArray arr = JSONArray.fromObject(result);
        obj = arr.getJSONObject(0);
        result = obj.getString("now");

        Gson gson = new Gson();
        Bean bean = gson.fromJson(dataString,Bean.class);
         **/
        final String urlxml = "https://dict-co.iciba.com/api/dictionary.php?w=" + english + "&key=9AA9FA4923AC16CED1583C26CF284C3F";

            HttpUtils.sendOkHttpRequest(urlxml, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Toast.makeText(TestVo.this, "获取翻译数据失败！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {

                    final String result = response.body().string();
                    Log.i(TAG, result);

                    runOnUiThread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            JsonEx.Envoice(result);
                            SharedPreferences pref = getSharedPreferences("JsonEx", MODE_PRIVATE);
                            final String voiceEnUrlText = pref.getString("voiceEnUrlText", "空");
                            ImageView enVoiceImg = (ImageView) findViewById(R.id.iv_en_voice);
                            enVoiceImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        MediaPlayer mediaPlayer = MediaPlayer.create(TestVo.this, Uri.parse(voiceEnUrlText));
                                        mediaPlayer.start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }
            });


    }


    private TextView.OnEditorActionListener EnterListenter = new TextView.OnEditorActionListener() {
        /**
         * 参数说明
         * @param v 被监听的对象
         * @param actionId  动作标识符,如果值等于EditorInfo.IME_NULL，则回车键被按下。
         * @param event    如果由输入键触发，这是事件；否则，这是空的(比如非输入键触发是空的)。
         * @return 返回你的动作
         */
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                Log.i("---","输入");
                String enString=EnString.toString();
                Log.i(TAG,"input===="+enString);
                if(enString == null || enString.equals("") || enString.equals(R.string.En)){//no input
                    ShowString.setText( "请输入英文单词");
                }else if(enString.equals(english)){
                    ShowString.setText("回答对啦");
                }else{
                    ShowString.setText("需要继续记忆"+english);
                    Log.i(TAG,english+enString);
                }
            }
            return false;
        }
    };

    public void submit(View view){
        EnString=(EditText)findViewById(R.id.ipt);
        String enString=EnString.getText().toString();
        Log.i(TAG,"input===="+enString);
        if(enString == null || enString.equals("") || enString.equals(R.string.hint)){//no input
            Toast.makeText(this, "请输入英文单词", Toast.LENGTH_SHORT).show();
        }else if(enString.equals(english)){
            Toast.makeText(this, "回答对啦", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "需要继续记忆", Toast.LENGTH_SHORT).show();
            ShowString.setText(english);
            Log.i(TAG,english+enString);
        }
        //Intent intent=new Intent(TestVo.this, TestVo.class);
        //startActivity(intent);
    }


    public void reM(View view){
        Intent intent=new Intent(TestVo.this, MainActivity.class);
        startActivity(intent);
    }
    public void ans(View view){
        ShowString.setText(english);
    }
    public void nex(View view){
        Intent intent=new Intent(TestVo.this, TestVo.class);
        startActivity(intent);
    }

}