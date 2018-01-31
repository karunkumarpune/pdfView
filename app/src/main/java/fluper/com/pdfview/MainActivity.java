package fluper.com.pdfview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.viewpagertransformers.StackTransformer;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.necistudio.vigerpdf.VigerPDF;
import com.necistudio.vigerpdf.adapter.VigerAdapter;
import com.necistudio.vigerpdf.manage.OnResultListener;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TextView tv_counter;
    private ArrayList<Bitmap> itemData;
    private VigerAdapter adapter;
    private Button btnFromFile, btnFromNetwork,btnCancle;
    private VigerPDF vigerPDF;
    ProgressDialog pd;
    int count=0;
    ArrayList<Integer> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("loading");
        pd.setCancelable(false);

        list=new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tv_counter = (TextView) findViewById(R.id.tv_counter);
        btnFromFile = (Button) findViewById(R.id.btnFile);
        btnFromNetwork = (Button) findViewById(R.id.btnNetwork);
        btnCancle = (Button)findViewById(R.id.btnCancle);
        vigerPDF = new VigerPDF(this);

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vigerPDF.cancle();
            }
        });
        btnFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withActivity(MainActivity.this)
                        .withRequestCode(100)
                        .withFilter(Pattern.compile(".*\\.pdf$"))
                        .start();
            }
        });

        btnFromNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemData.clear();
                adapter.notifyDataSetChanged();
                fromNetwork("http://www.pdf995.com/samples/pdf.pdf");
            }
        });


        itemData = new ArrayList<>();
        adapter = new VigerAdapter(getApplicationContext(), itemData);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new StackTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
             Toast.makeText(MainActivity.this, "read page: " + position+"/"+count, Toast.LENGTH_SHORT).show();
                tv_counter.setText(position+"/"+count);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            itemData.clear();
            adapter.notifyDataSetChanged();
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            fromFile(filePath);
           // pd.show();

        }
    }

    private void fromNetwork(String endpoint) {

        itemData.clear();
        adapter.notifyDataSetChanged();
        vigerPDF.cancle();
        vigerPDF.initFromNetwork(endpoint, new OnResultListener() {
            @Override
            public void resultData(Bitmap data) {
                Log.e("data", "run");
                itemData.add(data);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void progressData(int progress) {

                Log.e("data", "" + progress);
                Toast.makeText(getApplicationContext(),""+progress,Toast.LENGTH_SHORT).show();
            }


            @Override
            public void failed(Throwable t) {

            }

        });
    }

    private void fromFile(String path) {
        //pd.show();
        itemData.clear();
        adapter.notifyDataSetChanged();
        File file = new File(path);
        vigerPDF.cancle();
        vigerPDF.initFromFile(file, new OnResultListener() {
            @Override
            public void resultData(Bitmap data) {

                    itemData.add(data);
                    adapter.notifyDataSetChanged();
                    count++;
            }

            @Override
            public void progressData(int progress) {
                Log.e("data", "progressData" + progress);

            }

            @Override
            public void failed(Throwable t) {

            }

        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vigerPDF != null) vigerPDF.cancle();
    }
}