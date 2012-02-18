package com.markadamson.snakemon.free;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class AmbilWarnaPreference 
        extends Preference 
        implements Preference.OnPreferenceClickListener, OnAmbilWarnaListener {

        private ImageView ivColor;
        
        public AmbilWarnaPreference(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);
                
                init();
        }

        public AmbilWarnaPreference(Context context, AttributeSet attrs) {
                super(context, attrs);
                
                init();
        }
        
        public AmbilWarnaPreference(Context context)
        {
                super(context);
                
                init();
        }
        
        private void init()
        {
                setWidgetLayoutResource(R.layout.color_preference_widget);
                setOnPreferenceClickListener(this);
        }

        @Override
        protected void onBindView(View v)
        {
                super.onBindView(v);
                
                ivColor = (ImageView)v.findViewById(R.id.color);
                ivColor.setImageDrawable(getColorDrawable(getPersistedInt(Color.BLACK)));
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(getContext(), getPersistedInt(Color.BLACK), this);
                dialog.show();
                return true;
        }

        @Override
        public void onCancel(AmbilWarnaDialog dialog) {
        }

        @Override
        public void onOk(AmbilWarnaDialog dialog, int color) {
                ivColor.setImageDrawable(getColorDrawable(color));
                getEditor().putInt(getKey(), color).commit();
        }
        
        private Drawable getColorDrawable(int color)
        {
                return new LayerDrawable(
                                new Drawable[] {
                                                new ColorDrawable(color),
                                                getContext().getResources().getDrawable(R.drawable.color_preference_widget_frame)
                                });
        }
        
        
        
}