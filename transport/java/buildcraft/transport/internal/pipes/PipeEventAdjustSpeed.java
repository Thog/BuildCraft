package buildcraft.transport.internal.pipes;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeEventAdjustSpeed;
import buildcraft.api.transport.event.IPipeContents.IPipeContentsItem;

class PipeEventAdjustSpeed extends PipeEvent implements IPipeEventAdjustSpeed {
    private final IPipeContentsItem contents;
    float speed;

    PipeEventAdjustSpeed(IPipe pipe, IPipeContentsItem contents, float speed) {
        super(pipe);
        this.contents = contents;
        this.speed = speed;
    }

    @Override
    public IPipeContentsItem getContents() {
        return contents;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(float newSpeed) {
        speed = newSpeed;
    }

    @Override
    public void addSpeed(float toAdd, float min, float max, float maxChangeDiff) {
        if (speed > max) {
            if (toAdd >= 0) {
                speed -= Math.min(speed - max, maxChangeDiff);
            } else {
                speed -= toAdd;
            }
        } else if (speed < min) {
            if (toAdd <= 0) {
                speed += Math.min(speed - min, maxChangeDiff);
            } else {
                speed += toAdd;
            }
        } else {
            speed += toAdd;
        }
    }

    @Override
    public void multiplySpeed(float scalar, float min, float max, float maxChangeDiff) {
        if (speed > max) {
            if (scalar >= 1) {
                speed -= Math.min(speed - max, maxChangeDiff);
            } else {
                speed *= scalar;
            }
        } else if (speed < min) {
            if (scalar <= 1) {
                speed += Math.min(speed - min, maxChangeDiff);
            } else {
                speed *= scalar;
            }
        } else {
            speed *= scalar;
        }
    }
}
