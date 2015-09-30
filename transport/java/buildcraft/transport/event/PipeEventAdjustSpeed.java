package buildcraft.transport.event;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.event.IPipeContents.IPipeContentsItem;
import buildcraft.api.transport.event.IPipeEventAdjustSpeed;

public class PipeEventAdjustSpeed extends PipeEvent implements IPipeEventAdjustSpeed {
    private final IPipeContentsItem contents;
    private float speed;

    public PipeEventAdjustSpeed(IPipe pipe, IPipeContentsItem contents, float rawSpeed) {
        super(pipe);
        this.contents = contents;
        this.speed = pipe.getTransport().getPipeType().normaliseSpeed(rawSpeed);
    }

    @Override
    public IPipeContentsItem getContents() {
        return contents;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    public float getRawSpeed() {
        return getPipe().getTransport().getPipeType().denormaliseSpeed(speed);
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
