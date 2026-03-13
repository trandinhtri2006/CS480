import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Paints a polyline route on a JXMapViewer, correctly aligned with the viewport.
 */
public class RoutePainter implements Painter<JXMapViewer> {

    private final List<GeoPosition> track;

    public RoutePainter(List<GeoPosition> track) {
        this.track = track;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        if (track == null || track.size() < 2) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(4));

        Rectangle viewportBounds = map.getViewportBounds();

        Point2D prev = map.getTileFactory().geoToPixel(track.get(0), map.getZoom());
        prev.setLocation(prev.getX() - viewportBounds.getX(), prev.getY() - viewportBounds.getY());

        for (int i = 1; i < track.size(); i++) {
            Point2D curr = map.getTileFactory().geoToPixel(track.get(i), map.getZoom());
            curr.setLocation(curr.getX() - viewportBounds.getX(), curr.getY() - viewportBounds.getY());

            g2.drawLine((int) prev.getX(), (int) prev.getY(), (int) curr.getX(), (int) curr.getY());
            prev = curr;
        }

        g2.dispose();
    }
}