package buildcraft.api.transport.event;

import buildcraft.api.transport.event.IPipeContents.IPipeContentsItem;

public interface IPipeEventAdjustSpeed extends IPipeEvent {
    /** @return The current contents of the pipe. */
    IPipeContentsItem getContents();

    /** @return The current (normalised) speed */
    float getSpeed();

    /** @param newSpeed The new speed to set (MUST be more than 0.01) */
    void setSpeed(float newSpeed);

    /** @param toAdd The amount to increase the speed by. This won't change the speed if the current speed is outside of
     *            the min and max bounds, but it will change the speed to the min or max if it crossed the boundary
     *            between allowed and not.
     * @param min The minimum speed the items can be going at
     * @param max The maximum speed the items can be going at
     * @param maxChangeDiff The maximum amount to change the speed by, if it would need to change the speed to fit the
     *            min and max arguments */
    void addSpeed(float toAdd, float min, float max, float maxChangeDiff);

    /** @param multiplication The number to multiply the current speed by. This won't change the speed if the current
     *            speed is outside the boundary and this would push it further outside of the boundary.
     * @paramThe minimum speed the items can be going at
     * @param max The maximum speed the items can be going at
     * @param maxChangeDiff The maximum amount to change the speed by, if it would need to change the speed to fit the
     *            min and max arguments */
    void multiplySpeed(float multiplication, float min, float max, float maxChangeDiff);
}
