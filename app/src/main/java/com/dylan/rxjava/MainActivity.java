package com.dylan.rxjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    /** 多个一起绑定的时候是以列表的形式，控件修饰符不能为private或static */
    @BindViews({R.id.image_left, R.id.image_right})
    public List<ImageView> mImageList ;
//    @BindView(R.id.image)
//    public ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        loadImageByThread();
        loadImageByRxJava();
    }

    private void loadImageByThread() {
        new Thread() {

            @Override
            public void run() {
                final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_left);
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mImageList.get(0).setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }

    private void loadImageByRxJava() {
        Observable.create(new Observable.OnSubscribe<Bitmap>() {

            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_right);
                subscriber.onNext(bitmap);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Bitmap>() {

            @Override
            public void onNext(Bitmap bitmap) {
                mImageList.get(1).setImageBitmap(bitmap);
            }

            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
