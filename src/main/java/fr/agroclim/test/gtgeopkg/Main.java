package fr.agroclim.test.gtgeopkg;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.api.data.SimpleFeatureReader;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

public class Main {

    private static SimpleFeatureType createFeatureType() {
        final SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
        featureTypeBuilder.setName("Point");
        featureTypeBuilder.add("location", org.locationtech.jts.geom.Polygon.class, DefaultGeographicCRS.WGS84);
        featureTypeBuilder.add("cell", Integer.class);
        return featureTypeBuilder.buildFeatureType();
    }

    private static SimpleFeature createSimpleFeature(final SimpleFeatureType featureType) {
        final GeometryFactory geometryFactory = new GeometryFactory();
        final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        /* Longitude (= x coord) first ! */
        final Coordinate[] coordinates = {
                new Coordinate(1., 1.), //
                new Coordinate(2., 2.), //
                new Coordinate(2., 2.), //
                new Coordinate(2., 1.), //
                new Coordinate(1., 1.), //
        };
        featureBuilder.set("location", geometryFactory.createPolygon(coordinates));
        featureBuilder.set("cell", 1);
        featureBuilder.featureUserData(Hints.USE_PROVIDED_FID, Boolean.TRUE);
        return featureBuilder.buildFeature("cell.1");
    }

    private static void geotoolsMethods() throws IOException {

        final File file = File.createTempFile("geopkg", "db", new File("target"));
        file.deleteOnExit();

        try (final GeoPackage geopkg = new GeoPackage(file)) {
            geopkg.init();

            final SimpleFeatureType featureType = createFeatureType();

            final SimpleFeature feature = createSimpleFeature(featureType);
            final SimpleFeatureCollection featureCollection = new ListFeatureCollection(featureType, List.of(feature));

            final FeatureEntry entry = new FeatureEntry();
            entry.setDescription("Feature description");
            geopkg.add(entry, featureCollection);
            geopkg.createSpatialIndex(entry);

            //Once created, the features in the entry can be read using a SimpleFeatureReader:
            try(SimpleFeatureReader r = geopkg.reader(entry, null, null)) {
                while(r.hasNext()) {
                    final SimpleFeature sf = r.next();
                    assert sf != null;
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Logger.getLogger(Main.class.getName()).log(Level.INFO, "Start main.");
            geotoolsMethods();
            Logger.getLogger(Main.class.getName()).log(Level.INFO, "Execution done.");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
