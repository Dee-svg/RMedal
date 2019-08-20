package com.veni.tools.widget.progressing.style;

import android.animation.ValueAnimator;

import com.veni.tools.widget.progressing.animation.SpriteAnimatorBuilder;
import com.veni.tools.widget.progressing.sprite.CircleLayoutContainer;
import com.veni.tools.widget.progressing.sprite.CircleSprite;
import com.veni.tools.widget.progressing.sprite.Sprite;


/**
 * Created by ybq.
 * 圆圈 菊花
 */
public class Circle extends CircleLayoutContainer {

    @Override
    public Sprite[] onCreateChild() {
        Dot[] dots = new Dot[12];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new Dot();
            dots[i].setAnimationDelay(1200 / 12 * i + -1200);
        }
        return dots;
    }

    private class Dot extends CircleSprite {

        Dot() {
            setScale(0f);
        }

        @Override
        public ValueAnimator onCreateAnimation() {
            float fractions[] = new float[]{0f, 0.5f, 1f};
            return new SpriteAnimatorBuilder(this).
                    scale(fractions, 0f, 1f, 0f).
                    duration(1200).
                    easeInOut(fractions)
                    .build();
        }
    }
}
