package dawgsoft.br.lanterna;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity {

    ImageButton btLigaDesliga;
    TextView tvOn, tvOff;

    private Camera camera;
    private boolean flashLigado;
    private boolean temFlash;
    Camera.Parameters parametros;
    MediaPlayer som;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOn = (TextView) findViewById(R.id.textView);
        tvOff = (TextView) findViewById(R.id.textView2);
        btLigaDesliga = (ImageButton) findViewById(R.id.bt_liga_desliga);


        temFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!temFlash) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("ERRO");
            alert.setMessage("OPS! Parece que seu aparelho nao possui flash!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
            return;
        }

        getCamera();

        alteraImagemDoInterruptor();

        // clique do botao liga e/ou desliga
        btLigaDesliga.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (flashLigado) {
                    // desliga o flash
                    flashDesligado();
                } else {
                    // liga o flash
                    flashLigado();
                }
            }
        });
    }

    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                parametros = camera.getParameters();
            } catch (RuntimeException e) {
            }
        }
    }

    private void flashLigado() {
        if (!flashLigado) {
            if (camera == null || parametros == null) {
                return;
            }
            // chama o som de clique
            somDeClique();

            parametros = camera.getParameters();
            parametros.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parametros);
            camera.startPreview();
            flashLigado = true;

            // altera a imagem do interruptor
            alteraImagemDoInterruptor();
        }

    }

    private void flashDesligado() {
        if (flashLigado) {
            if (camera == null || parametros == null) {
                return;
            }
            // chama o som de clique
            somDeClique();

            parametros = camera.getParameters();
            parametros.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parametros);
            camera.stopPreview();
            flashLigado = false;

            // altera a imagem do interruptor
            alteraImagemDoInterruptor();
        }
    }


    // Som do clique
    // will play button toggle sound on flash on / off
    private void somDeClique(){
        if(flashLigado){
            som = MediaPlayer.create(MainActivity.this, R.drawable.light_switch_off);
        }else{
            som = MediaPlayer.create(MainActivity.this, R.drawable.light_switch_on);
        }
        som.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        som.start();
    }

    /*
     * Altera as imagens do interruptor para ligado e/ou desligado
     * */
    private void alteraImagemDoInterruptor(){
        if(flashLigado){
            tvOn.setTextColor(Color.parseColor("#FFFFFF"));
            tvOff.setTextColor(Color.parseColor("#000000"));
            btLigaDesliga.setImageResource(R.mipmap.btn_switch_on);
        }else{
            tvOn.setTextColor(Color.parseColor("#000000"));
            tvOff.setTextColor(Color.parseColor("#FFFFFF"));
            btLigaDesliga.setImageResource(R.mipmap.btn_switch_off);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        flashDesligado();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(temFlash)
            flashLigado();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

}
