import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Paints a polyline route on a JXMapViewer.
 */
public class RoutePainter implements Painter<JXMapViewer> {

    private final List<GeoPosition> track;

    public RoutePainter(List<GeoPosition> track) {
        this.track = track;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        if (track == null || track.size() < 2) return;

        g = (Graphics2D) g.create();

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(4));

        Point2D prev = map.getTileFactory().geoToPixel(track.get(0), map.getZoom());
        for (int i = 1; i < track.size(); i++) {
            Point2D curr = map.getTileFactory().geoToPixel(track.get(i), map.getZoom());
            g.drawLine((int) prev.getX(), (int) prev.getY(), (int) curr.getX(), (int) curr.getY());
            prev = curr;
        }

        g.dispose();
    }
}