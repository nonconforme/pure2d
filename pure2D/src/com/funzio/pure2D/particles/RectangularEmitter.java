/**
 * 
 */
package com.funzio.pure2D.particles;

import java.util.Random;

import android.graphics.PointF;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public class RectangularEmitter extends BaseDisplayObject implements ParticleEmitter {

    protected Random mRandom = new Random();
    protected boolean mFinished = false;
    protected boolean mRemoveOnFinish = false;
    protected int nNumParticles = 0;

    public boolean draw(final GLState glState) {
        // draw nothing
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#dispose()
     */
    @Override
    public void dispose() {

        // simple clean up
        nNumParticles = 0;
        mFinished = false;
        mRandom = null;
    }

    /**
     * This is more memory efficient
     * 
     * @param pt
     * @return
     */
    public PointF getNextPosition(final PointF pt) {
        pt.x = mPosition.x - mOrigin.x + mRandom.nextInt((int) mSize.x);
        pt.y = mPosition.y - mOrigin.y + mRandom.nextInt((int) mSize.y);

        return pt;
    }

    protected boolean addParticle(final Particle particle) {
        if (particle.getEmitter() != this) {
            nNumParticles++;

            // register listeners
            particle.setEmitter(this);

            // add particle to the same parent
            if (mParent != null) {
                mParent.addChild(particle);
            }
            return true;
        }

        return false;
    }

    protected boolean removeParticle(final Particle particle) {
        if (particle.getEmitter() == this) {
            nNumParticles--;

            // remove listener
            particle.setEmitter(null);

            // remove particle from parent
            if (mParent != null) {
                mParent.removeChild(particle);
            }

            // done?
            if (nNumParticles == 0) {
                finish();
            }

            return true;
        }

        return false;
    }

    public int getNumParticles() {
        return nNumParticles;
    }

    public void finish() {
        // check
        if (mFinished) {
            return;
        }

        mFinished = true;
        if (mRemoveOnFinish) {
            // auto remove me
            removeFromParent();
        }
    }

    public void queueFinish() {
        queueEvent(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        });
    }

    public boolean isFinished() {
        return mFinished;
    }

    /**
     * @return the removeOnFinish
     */
    public boolean isRemoveOnFinish() {
        return mRemoveOnFinish;
    }

    /**
     * @param removeOnFinish the removeOnFinish to set
     */
    public void setRemoveOnFinish(final boolean removeOnFinish) {
        mRemoveOnFinish = removeOnFinish;
    }

    @Override
    public void onParticleFinish(final Particle particle) {
        queueEvent(new Runnable() {

            @Override
            public void run() {
                // auto remove
                removeParticle(particle);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onRemoved()
     */
    @Override
    public void onRemoved() {
        super.onRemoved();

        // clean up
        dispose();
    }
}
