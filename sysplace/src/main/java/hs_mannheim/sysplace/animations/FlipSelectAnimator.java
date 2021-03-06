/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *     Hochschule Mannheim University of Applied Sciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package hs_mannheim.sysplace.animations;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import hs_mannheim.gestureframework.animation.GestureAnimator;
import hs_mannheim.gestureframework.animation.BitmapHelper;
import hs_mannheim.sysplace.R;

public class FlipSelectAnimator extends GestureAnimator {

    private Animator mFlipLeftInAnimator, mFlipLeftOutAnimator, mElevateAnimator, mLowerAnimator;
    private BitmapHelper mBitmapHelper;
    private ImageView mImageViewCopy;
    private Bitmap mPolaroidFrame;


    public FlipSelectAnimator(Context context, View view) {
        super(context, view);

        mPolaroidFrame = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.polaroid_frame);
        mBitmapHelper = new BitmapHelper();


        //TODO: Hacky
        Activity activity = (Activity)mContext;
        mImageViewCopy = (ImageView) activity.findViewById(R.id.imgViewCopy);
    }

    @Override
    public void play() {
        mElevateAnimator.start();
    }

    @Override
    protected void registerAnimators() {
        mElevateAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.elevate);
        mElevateAnimator.addListener(this);
        mElevateAnimator.setTarget(mView);
        mElevateAnimator.setDuration(500);

        mFlipLeftOutAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.flip_left_out);
        mFlipLeftOutAnimator.addListener(this);
        mFlipLeftOutAnimator.setTarget(mView);

        mFlipLeftInAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.flip_left_in);
        mFlipLeftInAnimator.addListener(this);
        mFlipLeftInAnimator.setTarget(mView);

        mLowerAnimator = AnimatorInflater.loadAnimator(mContext, R.animator.lower);
        mLowerAnimator.addListener(this);
        mLowerAnimator.setTarget(mView);
        mLowerAnimator.setDuration(500);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if (animator == mElevateAnimator) {
            mFlipLeftOutAnimator.start();
        }

        if (animator == mFlipLeftOutAnimator) {
            mBitmapHelper.updateImageView(mContext, (ImageView) mView, mReplacementBitmap, mPolaroidFrame);

            
            mFlipLeftInAnimator.start();
        }

        if (animator == mFlipLeftInAnimator) {
            mBitmapHelper.updateImageView(mContext, mImageViewCopy, mReplacementBitmap, mPolaroidFrame);
            //mLowerAnimator.start();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}