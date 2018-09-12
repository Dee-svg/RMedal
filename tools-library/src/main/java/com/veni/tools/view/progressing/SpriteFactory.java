package com.veni.tools.view.progressing;

import com.veni.tools.view.progressing.sprite.Sprite;
import com.veni.tools.view.progressing.style.ChasingDots;
import com.veni.tools.view.progressing.style.Circle;
import com.veni.tools.view.progressing.style.CubeGrid;
import com.veni.tools.view.progressing.style.DoubleBounce;
import com.veni.tools.view.progressing.style.FadingCircle;
import com.veni.tools.view.progressing.style.FoldingCube;
import com.veni.tools.view.progressing.style.MultiplePulse;
import com.veni.tools.view.progressing.style.MultiplePulseRing;
import com.veni.tools.view.progressing.style.Pulse;
import com.veni.tools.view.progressing.style.PulseRing;
import com.veni.tools.view.progressing.style.RotatingCircle;
import com.veni.tools.view.progressing.style.RotatingPlane;
import com.veni.tools.view.progressing.style.ThreeBounce;
import com.veni.tools.view.progressing.style.WanderingCubes;
import com.veni.tools.view.progressing.style.Wave;

/**
 * Created by ybq.
 * 0   ROTATING_PLANE 平面上下左右旋转
 * 1   DOUBLE_BOUNCE 两点上下晃动
 * 2   WAVE 波浪
 * 3   WANDERING_CUBES 对角旋转正方体
 * 4   PULSE 脉冲
 * 5   CHASING_DOTS 点追逐
 * 6   THREE_BOUNCE 三点上下晃动
 * 7   CIRCLE 圆圈 菊花
 * 8   CUBE_GRID 正方体网格
 * 9   FADING_CIRCLE 衰退圆圈
 * 10  FOLDING_CUBE 折叠正方体
 * 11  ROTATING_CIRCLE 圆圈上下左右旋转
 * 12  MULTIPLE_PULSE 复杂脉冲
 * 13  PULSE_RING 环形脉冲
 * 14  MULTIPLE_PULSE_RING 复杂环形脉冲
 *
 *
 */
public class SpriteFactory {

    public static Sprite create(Style style) {
        Sprite sprite = null;
        switch (style) {
            case ROTATING_PLANE:
                sprite = new RotatingPlane();
                break;
            case DOUBLE_BOUNCE:
                sprite = new DoubleBounce();
                break;
            case WAVE:
                sprite = new Wave();
                break;
            case WANDERING_CUBES:
                sprite = new WanderingCubes();
                break;
            case PULSE:
                sprite = new Pulse();
                break;
            case CHASING_DOTS:
                sprite = new ChasingDots();
                break;
            case THREE_BOUNCE:
                sprite = new ThreeBounce();
                break;
            case CIRCLE:
                sprite = new Circle();
                break;
            case CUBE_GRID:
                sprite = new CubeGrid();
                break;
            case FADING_CIRCLE:
                sprite = new FadingCircle();
                break;
            case FOLDING_CUBE:
                sprite = new FoldingCube();
                break;
            case ROTATING_CIRCLE:
                sprite = new RotatingCircle();
                break;
            case MULTIPLE_PULSE:
                sprite = new MultiplePulse();
                break;
            case PULSE_RING:
                sprite = new PulseRing();
                break;
            case MULTIPLE_PULSE_RING:
                sprite = new MultiplePulseRing();
                break;
            default:
                break;
        }
        return sprite;
    }
}
