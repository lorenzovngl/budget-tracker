package it.unibo.studio.vainigli.lorenzo.budgettracker.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;

public class IconsActivity extends AppCompatActivity {

    private final int ICON_HEIGHT = 175;
    private final int ICON_WIDTH = 175;
    private final int ICON_PADDING = 20;
    public static final String CHOOSED_ICON_NAME = "choosed_icon_name";
    public static final String CHOOSED_ICON_ID = "choosed_icon_id";
    private ViewGroup layout;
    private int layoutWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_categories_icons);
        layout = (ViewGroup) findViewById(R.id.categoriesIcons);
        // Observer per ottenere le misure del layout
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layoutWidth = layout.getMeasuredWidth();
                Log.i("WIDTH", Integer.toString(layoutWidth));
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                createIcons();
            }
        });
    }

    private void createIcons(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle extras = getIntent().getExtras();
        int icon = 0;
        if (extras != null){
            icon = extras.getInt(CHOOSED_ICON_ID);
        }
        final Resources resources = getResources();
        TypedArray typedArray = resources.obtainTypedArray(R.array.categ_icons);
        int length = typedArray.length();
        int i = 0;
        final List<ImageView> imageViewList = new ArrayList<ImageView>();
        while (i < length){
            View tableRow = inflater.inflate(R.layout.tablerow_categories_icons, null);
            int count = ICON_WIDTH;
            while (count < layoutWidth && i < length){
                final int imageId = typedArray.getResourceId(i, 0);
                final ImageView imageView = (ImageView) inflater.inflate(R.layout.imageview_icon, null);
                imageView.setTag(R.string.icon_name, typedArray.getString(i));
                imageView.setTag(R.string.icon_id, imageId);
                Log.i("IMG", imageView.getTag(R.string.icon_name).toString());
                imageView.setImageResource(imageId);
                imageView.setMinimumHeight(ICON_HEIGHT);
                imageView.setMinimumWidth(ICON_WIDTH);
                imageView.setPadding(ICON_PADDING, ICON_PADDING, ICON_PADDING, ICON_PADDING);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < imageViewList.size(); i++){
                            imageViewList.get(i).setBackgroundResource(0);
                        }
                        view.setBackgroundResource(R.drawable.image_border);
                        Intent returnIntent = new Intent();
                        Log.i("ICON NAME", (String) view.getTag(R.string.icon_name));
                        returnIntent.putExtra(CHOOSED_ICON_NAME, (String) view.getTag(R.string.icon_name));
                        returnIntent.putExtra(CHOOSED_ICON_ID, (int) view.getTag(R.string.icon_id));
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                });
                if (icon != 0 && icon == imageId){
                    imageView.setBackgroundResource(R.drawable.image_border);
                }
                ((ViewGroup) tableRow).addView(imageView);
                imageViewList.add(imageView);
                count += ICON_WIDTH;
                i++;
            }
            layout.addView(tableRow);
        }
    }
}
