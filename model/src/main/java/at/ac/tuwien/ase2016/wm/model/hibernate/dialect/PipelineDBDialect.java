package at.ac.tuwien.ase2016.wm.model.hibernate.dialect;

import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.spi.Exporter;

public class PipelineDBDialect extends PostgreSQL94Dialect {

    private final PipelineDBTableExporter tableExporter = new PipelineDBTableExporter(this);

    @Override
    public Exporter<Table> getTableExporter() {
        return tableExporter;
    }
}
