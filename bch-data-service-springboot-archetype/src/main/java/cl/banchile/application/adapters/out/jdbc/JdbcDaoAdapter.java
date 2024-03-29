package cl.banchile.application.adapters.out.jdbc;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import cl.banchile.domain.model.domain.DetalleModel;
import cl.banchile.domain.model.domain.MaestroModel;
import cl.banchile.domain.model.enums.MaestroTipoEnum;
import cl.banchile.domain.ports.out.DatabasePort;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador que implementa el puerto de acceso a Base de datos
 * Qualifier espcífico ya que en arquetipo base hay 2 benas que implementanm la interface
 * DatabasePort
 */
@Slf4j
@Component
@Qualifier("jdbcDao")
public class JdbcDaoAdapter implements DatabasePort{

    private JdbcTemplate jdbcTemplate;

    /**
     * Constructor con inyección de dependecias
     * @param jdbcTemplate inyección del template autoconfigurado de Springboot para acceso a datos por JDBC
     */
    public JdbcDaoAdapter(
        @Autowired JdbcTemplate jdbcTemplate
    ){
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Método para obtener detalle por identificador
     * utiliza JdbcTemplate
     * Para evidenciar que resultado puede no existir, retorna Optional
     */
    @Override
    public Optional<DetalleModel> getDetalleById(Long idDetalle){
        String query = "SELECT id, cnt, fec, mnt, id_mae FROM tglo_detalle where id = ?";
        return Optional.ofNullable(
            jdbcTemplate.queryForObject(
                query,
                new RowMapper<DetalleModel>(){
                    @Override
                    public DetalleModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DetalleModel.builder()
                            .id(rs.getLong("id"))
                            .cantidad(rs.getInt("cnt"))
                            .fecha(Optional.ofNullable(rs.getDate("fec", Calendar.getInstance())).map(Date::toLocalDate).orElse(null))
                            .monto(rs.getDouble("mnt"))
                            .idMaestro(rs.getLong("id_mae"))
                        .build();
                    }
                },
                idDetalle
            )
        );
    }

    /**
     * Método para obtener todos los detalles registrados
     * utiliza JdbcTemplate
     */
    @Override
    public List<DetalleModel> getAllDetalles(){
        String query = "SELECT id, cnt, fec, mnt, id_mae FROM tglo_detalle";

        return jdbcTemplate.queryForStream(
            query,
            new RowMapper<DetalleModel>(){
                @Override
                public DetalleModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return DetalleModel.builder()
                        .id(rs.getLong("id"))
                        .cantidad(rs.getInt("cnt"))
                        .fecha(Optional.ofNullable(rs.getDate("fec", Calendar.getInstance())).map(Date::toLocalDate).orElse(null))
                        .monto(rs.getDouble("mnt"))
                        .idMaestro(rs.getLong("id_mae"))
                    .build();
                }
            }
        ).collect(Collectors.toList());

    }

    /**
     * Método para insertar un detalle
     * utiliza JdbcTemplate
     */
    @Override
    public DetalleModel insertDetalle(DetalleModel detalleModel){
        String querySequence = "select SA_FOL_FIC.NEXTVAL seq from dual";
        String query = "INSERT INTO tglo_detalle(id, cnt, fec, mnt, id_mae) values(?, ?, ?, ?, ?)";

        Long sequenceNumber = jdbcTemplate.queryForObject(querySequence, Long.class);
        detalleModel.setId(sequenceNumber);
        jdbcTemplate.update(query, sequenceNumber, detalleModel.getCantidad(), detalleModel.getFecha(), detalleModel.getMonto(), detalleModel.getIdMaestro());

        // Retornando un clon del detalle, incluyendo el Id generado
        return DetalleModel.builder()
            .id(detalleModel.getId())
            .idMaestro(detalleModel.getIdMaestro())
            .cantidad(detalleModel.getCantidad())
            .fecha(detalleModel.getFecha())
            .monto(detalleModel.getMonto())
        .build();
    }

    /**
     * Método para actualizar un detalle
     * utiliza JdbcTemplate
     */
    @Override
    public DetalleModel updateDetalle(DetalleModel detalleModel){
        String query = "UPDATE tglo_detalle SET cnt = ?, fec = ?, mnt = ?, id_mae = ? WHERE id = ?";

        jdbcTemplate.update(query, detalleModel.getCantidad(), detalleModel.getFecha(), detalleModel.getMonto(), detalleModel.getIdMaestro(), detalleModel.getId());

        return detalleModel;
    }

    /**
     * Método Obtener para EliminarDetalle
     * utiliza JdbcTemplate
     */
    @Override
    public void deleteDetalle(Long id){
        String query = "DELETE FROM tglo_detalle WHERE id = ?";
        jdbcTemplate.update(query, id);
    }


    // Métodos de persistencia de maestro
    
    /**
     * Método para obtener un maestro por identificador
     * utiliza JdbcTemplate
     * RetornaOptional para evidenciar que puede no existir
     */
    @Override
    public Optional<MaestroModel> getMaestroById(Long idMaestro){
        MaestroModel model = null;
        String query = "SELECT id, nom, des, tip FROM tglo_maestro where id = ?";

        try{
            model = jdbcTemplate.queryForObject(
                query,
                new RowMapper<MaestroModel>(){
                    @Override
                    public MaestroModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return MaestroModel.builder()
                            .id(rs.getLong("id"))
                            .nombre(rs.getString("nom"))
                            .descripcion(rs.getString("des"))
                            .tipo(MaestroTipoEnum.of(rs.getInt("tip")))
                        .build();
                    }
                },
                idMaestro
            );
        }
        catch(EmptyResultDataAccessException ex){
            // Capturando la excepción al encontrar un resultado vacío
            //retornará un Optional Vacío en ven de una excepción en Runtime
            log.debug("Resultado vacío", ex);
        }
        
        return Optional.ofNullable(model);
    }

    /**
     * Método para obtener todos los maestros registrados
     * utiliza JdbcTemplate
     */
    @Override
    public List<MaestroModel> getAllMaestros(){
        String query = "SELECT id, nom, des, tip FROM tglo_maestro";

        return jdbcTemplate.queryForStream(
            query,
            new RowMapper<MaestroModel>(){
                @Override
                public MaestroModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return MaestroModel.builder()
                        .id(rs.getLong("id"))
                        .nombre(rs.getString("nom"))
                        .descripcion(rs.getString("des"))
                        .tipo(MaestroTipoEnum.of(rs.getInt("tip")))
                    .build();
                }
            }
        ).collect(Collectors.toList());

    }
    
    /**
     * Método para insertar un Maestro
     * utiliza JdbcTemplate
     */
    @Override
    public MaestroModel insertMaestro(MaestroModel maestroModel){
        String querySequence = "select SA_FOL_FIC.nextval from dual";
        String query = "INSERT INTO tglo_maestro(id, nom, des, tip) values(?, ?, ?, ?)";

        Long sequenceNumber = jdbcTemplate.queryForObject(querySequence, Long.class);
        maestroModel.setId(sequenceNumber);

        jdbcTemplate.update(query, sequenceNumber, maestroModel.getNombre(), maestroModel.getDescripcion(), maestroModel.getTipo().getCodigo());


        return MaestroModel.builder()
            .id(maestroModel.getId())
            .nombre(maestroModel.getNombre())
            .descripcion(maestroModel.getDescripcion())
            .tipo(maestroModel.getTipo())
        .build();

    }


    /**
     * Método para Eliminar un maestro
     * utiliza JdbcTemplate
     */
    @Override
    public void deleteMaestro(Long id){
        String query = "DELETE FROM tglo_maestro WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    /**
     * Método actualizar un maestro
     * utiliza JdbcTemplate
     */
    @Override
    public MaestroModel updateMaestro(MaestroModel maestroModel){
        String query = "UPDATE tglo_maestro SET nom = ?, des = ?, tip = ? WHERE id = ?";

        jdbcTemplate.update(query, maestroModel.getNombre(), maestroModel.getDescripcion(), maestroModel.getTipo().getCodigo(), maestroModel.getId());

        return MaestroModel.builder()
            .id(maestroModel.getId())
            .nombre(maestroModel.getNombre())
            .descripcion(maestroModel.getDescripcion())
            .tipo(maestroModel.getTipo())
        .build();
    }
}
