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
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    /** 多个一起绑定的时候是以列表的形式，控件修饰符不能为private或static */
    @BindViews({R.id.image_left, R.id.image_right})
    public List<ImageView> mImageList ;
//    @BindView(R.id.image)
//    public ImageView mImage;

    private int mIdIcLeft = R.drawable.ic_left;
    private int mIdIcRight = R.drawable.ic_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        loadImageByThread();
        loadImageByRxJava2();
    }

    private void loadImageByThread() {
        new Thread() {

            @Override
            public void run() {
                final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mIdIcLeft);
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
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mIdIcRight);
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
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImageByRxJava2() {
        Observable.from(new Integer[]{mIdIcRight})
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
                mImageList.get(1).setImageBitmap(bitmap);
            }
        }, new Action1<Throwable>() {  //onNError

            @Override
            public void call(Throwable e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        }, new Action0() {  //onComplete

            @Override
            public void call() {

            }
        });
    }
}
