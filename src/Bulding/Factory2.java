//package Bulding;
//
//import java.awt.*;
//
//public class Factory2 extends Bulding {
//    {
//        setName("Factory");
//        setCollectedSteel(2000);
//        setPower(-100);
//        setHeight(55);
//        setWidth(55);
//        setLive(10);
//    }
//
//    public void draw(Graphics g) {
//        g.setColor(Color.RED);
//        g.fillRect(x, y, getWidth(), getHeight());
//
//        g.setColor(Color.BLACK);
//        g.drawString(getName(), x + getWidth() / 2 - 19, y + getHeight() / 2 + 5);
//
//        if (selected) {
//            g.setColor(Color.GRAY);
//            g.drawRect(x - 2, y - 2, getWidth() + 4, getHeight() + 4);
//        }
//    }
//}