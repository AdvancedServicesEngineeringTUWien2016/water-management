package at.ac.tuwien.ase2016.wm.model.hibernate.dialect;

import at.ac.tuwien.ase2016.wm.model.Configuration;
import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.internal.StandardTableExporter;

public class PipelineDBTableExporter extends StandardTableExporter {

    public PipelineDBTableExporter(Dialect dialect) {
        super(dialect);
    }

    @Override
    public String[] getSqlCreateStrings(Table table, Metadata metadata) {
        if (Configuration.usePipelineDb) {
            // Skip creation of a table for the stream
            if ("micro_dc_sensor_event".equals(table.getName().toLowerCase())) {
                return new String[0];
            }
        }
        return super.getSqlCreateStrings(table, metadata);
    }

    @Override
    public String[] getSqlDropStrings(Table table, Metadata metadata) {
        if (Configuration.usePipelineDb) {
            // Skip creation of a table for the stream
            if ("micro_dc_sensor_event".equals(table.getName().toLowerCase())) {
                return new String[0];
            }
        }
        return super.getSqlDropStrings(table, metadata);
    }
}
