package com.adigastudio.kodesoalguru.databinding;

import android.content.Context;
import android.view.View;

import com.adigastudio.kodesoalguru.R;
import com.adigastudio.kodesoalguru.repositories.QuestionRepository;
import com.adigastudio.kodesoalguru.utils.GlideApp;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.StorageReference;

import androidx.databinding.BindingAdapter;

public class GlideBindingAdapters {

    @BindingAdapter("imageResource")
    public static void setImageResource(com.github.chrisbanes.photoview.PhotoView view, String imageUrl){
        String TAG = "GlideBindingAdapters";
        if (imageUrl == null || imageUrl.equals("")) {
            view.setVisibility(View.GONE);
            return;
        }
        view.setVisibility(View.VISIBLE);
        Context context = view.getContext().getApplicationContext();
        StorageReference storageRef = new QuestionRepository().getFirebaseStorageInstance().getReference();
        StorageReference imagesRef = storageRef.child(imageUrl);

        RequestOptions option = new RequestOptions()
                .placeholder(R.drawable.ic_error_24dp)
                .error(R.drawable.ic_error_24dp);

        GlideApp.with(context)
                .setDefaultRequestOptions(option)
                .load(imagesRef)
                .override(600)
                .fitCenter()
                .into(view);
    }

}
























