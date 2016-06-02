package com.example.rhaleff.enviarreclamacao;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mAssunto;
    private EditText mReclamacao;
    private ImageView mBotaoEnviar;
    private ImageView mCamera;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = (EditText) findViewById(R.id.editTextEmail);
        mAssunto = (EditText) findViewById(R.id.editTextTitulo);
        mReclamacao = (EditText) findViewById(R.id.editTextReclamacao);
        mBotaoEnviar = (ImageView) findViewById(R.id.botaoEnviar);
        mCamera = (ImageView) findViewById(R.id.fotoCamera);

        mBotaoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();

            }
        });
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamera();
            }
        });
    }

     private void sendEmail() {

         String email = mEmail.getText().toString();
         String assunto = mAssunto.getText().toString();
         String reclamacao = mReclamacao.getText().toString();
         String path = getOriginalImagePath();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(assunto) && !TextUtils.isEmpty(reclamacao)){
            Reclamacao reclam = new Reclamacao(reclamacao,email);
            EnviarEmail sm = new EnviarEmail(this, assunto, reclam, path);
            sm.execute();
            //intent telafinal

        }else{
            View focus = null;

            if(TextUtils.isEmpty(email)){
                this.mEmail.setError("Campo Vazio");
                focus = this.mEmail;
                focus.requestFocus();
            }

            if(TextUtils.isEmpty(assunto)){
                this.mAssunto.setError("Campo Vazio");
                focus = this.mAssunto;
                focus.requestFocus();
            }
            if(TextUtils.isEmpty(reclamacao)){
                this.mReclamacao.setError("Campo Vazio");
                focus = this.mReclamacao;
                focus.requestFocus();
            }

        }

    }

    private void abrirCamera() {

        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        InputStream stream = null;

        try {
            if(requestCode == 0 && resultCode == RESULT_OK){
                bitmap = (Bitmap) data.getExtras().get("data");
                mCamera.setImageBitmap(bitmap);
            }
            //implementar a foto da galeria
            if (requestCode == 1 && resultCode == RESULT_OK) {
               if (bitmap != null) {
                    bitmap.recycle();
                }

                stream = getContentResolver().openInputStream(data.getData());
                Bitmap bm = BitmapFactory.decodeStream(stream);

                mCamera.setImageBitmap(bm);

            }
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public String getOriginalImagePath() {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = super.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToLast();

        return cursor.getString(column_index_data);
    }

}