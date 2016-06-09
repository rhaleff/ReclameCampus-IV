package com.example.rhaleff.enviarreclamacao;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
    private String path = " ";


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
                if(temConexao(MainActivity.this)){
                    sendEmail();
                }else{
                    Toast.makeText(getApplicationContext(),"Sem Conexão com a internet!", Toast.LENGTH_SHORT).show();
                }

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

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(assunto) && !TextUtils.isEmpty(reclamacao)){
            Reclamacao reclam = new Reclamacao(reclamacao,email);
            EnviarEmail sm = new EnviarEmail(this, assunto, reclam, this.path);
            sm.execute();

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

    private boolean temConexao(Context classe){
        //Pego a conectividade do contexto passado como argumento
        ConnectivityManager gerenciador = (ConnectivityManager) classe.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Crio a variável informacao que recebe as informações da Rede
        NetworkInfo informacao = gerenciador.getActiveNetworkInfo();
        //Se o objeto for nulo ou nao tem conectividade retorna false
        if ((informacao != null) && (informacao.isConnectedOrConnecting()) && (informacao.isAvailable())) {
            return true;
        }
        return false;
    }

    private void abrirCamera() {

        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 0);
    }

    /*/private void abrirGaleria(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Complete a ação usando"), 1);

    }/*/

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        InputStream stream = null;

        try {
            if(requestCode == 0 && resultCode == RESULT_OK){

                String[] largeFileProjection = { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA };
                String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
                Cursor myCursor = this.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, largeFileProjection, null, null, largeFileSort);
                if(myCursor!=null) {
                    myCursor.moveToFirst();
                    this.path = myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                    myCursor.close();
                    Log.e(this.path,"Caminho Foto");
                }

                bitmap = (Bitmap) data.getExtras().get("data");
                mCamera.setImageBitmap(bitmap);

            }
            //falta implementar a foto da galeria
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
       return "";
    }


}