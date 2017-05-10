package com.dylan.rxjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    @BindViews({R.id.image_left, R.id.image_middle, R.id.image_right})
    public List<ImageView> mImageList;

    @BindView(R.id.image_left)
    public ImageView mImageLeft;

    private ImageView mImageRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mImageRight = (ImageView) findViewById(R.id.image_right);

        loadImageByThread();
        loadImageByRxJava1();
        loadImageByRxJava2();
    }

    private void loadImageByThread() {
        new Thread() {

            @Override
            public void run() {
                final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_left);
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mImageLeft.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }

    private void loadImageByRxJava1() {
        Observable.create(new Observable.OnSubscribe<Bitmap>() {

            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_middle);
                subscriber.onNext(bitmap);
                subscriber.onCompleted();
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
            public void onCompleted() {
                Toast.makeText(MainActivity.this, "Complete!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImageByRxJava2() {
        Observable.from(new Integer[]{R.drawable.ic_right})
        .map(new Func1<Integer, Bitmap>() {

            @Override
            public Bitmap call(Integer idIcRight) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), idIcRight);
                return bitmap;
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Bitmap>() {  //onNext

            @Override
            public void call(Bitmap bitmap) {
                mImageRight.setImageBitmap(bitmap);
            }
        }, new Action1<Throwable>() {  //onError

            @Override
            public void call(Throwable e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        }, new Action0() {  //onComplete

            @Override
            public void call() {
                Toast.makeText(MainActivity.this, "Complete!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
