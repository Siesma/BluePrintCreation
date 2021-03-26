import processing.core.PApplet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class dataCreator extends PApplet {
    public static void main(String[] args) {
        PApplet.main("combined", args);
    }


    private static final int LEN = 356;
    private static final double PHI = (1 + sqrt(5)) / 2;
    private static final int OFFSET = 40;
    private int amount = 0;
    private double ang = (PI / 5) * amount;
    private final String[] VAL = { "One", "Two", "Three", "Four", "Five" };

    @Override
    public void setup() {
        increaseAngle();
    }

    @Override
    public void settings() {
        size(LEN * 2 - OFFSET * 2, LEN * 2 - OFFSET * 2);
    }

    void increaseAngle() {
        ang += PI / 5;
        renderShape();
    }

    void saveData(int amount) {
        try {
            File f = new File("My_File_" + VAL[amount] + "_Combined.txt");
            this.amount++;
            f.createNewFile();
            FileWriter fileWriter = new FileWriter(f);
            String text = "";
            ArrayList<String> texts = new ArrayList<>();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    text += pixels[i + j * width] == -1 ? "1" : "0";
                }
                texts.add(text);
                text = "";
            }
            String[] names = { "refined-concrete", "refined-hazard-concrete-left" };
            ArrayList<String> partials = new ArrayList<>();
            String out = "";
            int x = 0, y = 0;
            for (String lines : texts) {
                for (char chars : lines.toCharArray()) {
                    String test = names[Integer.parseInt(chars + "")];
                    out += "{\"position\": {\"x\": " + x + ", \"y\": " + y + " }, \"name\": \"" + test + "\"},";
                    x++;
                }
                x = 0;
                y++;
                partials.add(out);
                out = "";
            }
            partials.set(partials.size() - 1, partials.get(partials.size() - 1).substring(0,
                    partials.get(partials.size() - 1).length() - 1));
            /**
             addition represents the beginning of the blueprint. It also handles the four small preview items
             */
            String addition = "{\"blueprint\": {\"icons\": [{\"signal\": {\"type\": \"item\",\"name\": \"refined-concrete\"},\"index\": 1}],\"tiles\":[";
            /**
             ending represents the ending of the blueprint. It just handles the information needed to close the blueprint
             */
            String ending = "],\"item\": \"blueprint\",\"version\": 281479273447424}}";
            fileWriter.write(addition);
            for (String s : partials)
                fileWriter.write(s);
            fileWriter.write(ending);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An Error occured! Either the file creating failed or the valindex was invalid");
            System.exit(1);
        }

    }

    public void renderShape() {
        background(128);
        Vec kiteA, kiteB, kiteC, kiteD, kiteE, center;
        double cos_ts = Math.cos(Math.toRadians(36));
        double sin_ts = Math.sin(Math.toRadians(36));
        center = new Vec(width / 2, height / 2);
        double c = LEN / PHI;
        double a = Math.sin(Math.toRadians(18)) * c;


        kiteA = new Vec(center.x - (c), center.y);
        kiteB = new Vec(center.x + (1 / (LEN / a)) * LEN, center.y + LEN * sin_ts);
        kiteC = new Vec(center.x + (1 / (LEN / a)) * LEN, center.y - LEN * sin_ts);
        kiteD = new Vec(center.x, center.y);
        kiteA = rotate_point((float) center.x, (float) center.y, (float) ang, kiteA);
        kiteB = rotate_point((float) center.x, (float) center.y, (float) ang, kiteB);
        kiteC = rotate_point((float) center.x, (float) center.y, (float) ang, kiteC);
        kiteD = rotate_point((float) center.x, (float) center.y, (float) ang, kiteD);
        Kite kite = new Kite(
                new Triangle(kiteA, kiteD, kiteB),
                new Triangle(kiteA, kiteD, kiteC),
                new Triangle(kiteA, kiteD, kiteC),
                new Triangle(kiteA, kiteD, kiteC)
        );
        kite.render();
        if (amount < 5) {
            saveData(amount);
            increaseAngle();
        } else {
            System.out.println("Finished!");
            System.exit(1);
        }
    }

    abstract class Shape {
        Triangle a, b, c, d;

        Shape(Triangle a, Triangle b, Triangle c, Triangle d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        abstract void render();
    }

    class Kite extends Shape {
        Kite(Triangle a, Triangle b, Triangle c, Triangle d) {
            super(a, b, c, d);
        }


        @Override
        void render() {
            loadPixels();
            /**
            allowance handles the given deviation to the shape. Smaller values give a more exact result, but can also
             contain smaller artifacts. Allowance cant be equal to 0 as that would fail with the floating point
             precision.
             */
            double allowance = 0.0000001;
            for (int i = 0; i < pixels.length; i++) {
                int x = i % width;
                int y = i / height;
                boolean flag = false;
                Vec p = new Vec(x, y);
                /**
                can be used to create more creative prints.
                 (example) ->
                 int flag = -1;
                 flag = a.distTo(p) + b.distTo(p) + c.distTo(p) + d.distTo(p) * RANGE_CONSTANT // has to be big: ~10^8;
                 pixels[i] = flag == -1 ? -1 : color(flag);
                 this will create a grey_scaled image of the blur of the given shape.
                 the function distTo could be modified to not take the abs() value of the distance. Using that could
                 result in this function to create just the outline and not a fully filled shape.
                 */
                if(a.distTo(p) + b.distTo(p) + c.distTo(p) + d.distTo(p) <= allowance * 4)
                    flag = true;
                if (flag) {
                    pixels[i] = -1;
                } else {
                    pixels[i] = color(0);
                }
            }
            updatePixels();

        }

    }

    Vec rotate_point(float cx, float cy, float angle, Vec p) {

        return new Vec(cos(angle) * (p.x - cx) - sin(angle) * (p.y - cy) + cx,
                sin(angle) * (p.x - cx) + cos(angle) * (p.y - cy) + cy);
    }


    class Triangle {
        Vec a, b, c;

        Triangle(Vec a, Vec b, Vec c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        double distTo(Vec p) {
            double areaABC = area(a, b, c);
            double areaPBC = area(p, b, c);
            double areaPAC = area(p, a, c);
            double areaPAB = area(p, a, b);
            return Math.abs(areaABC - (areaPAC + areaPBC + areaPAB));
        }

        private double area(Vec a, Vec b, Vec c) {
            return Math.abs(((a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2.0));
        }
    }

    class Vec {
        double x, y;

        Vec(double x, double y) {
            this.x = x;
            this.y = y;
        }

        void render(Vec center) {
            fill(255, 0, 0, 255);
            text((this.x - center.x) + " | " + (this.y - center.y), (float) this.x, (float) this.y);
            fill(0);
        }
    }


}
