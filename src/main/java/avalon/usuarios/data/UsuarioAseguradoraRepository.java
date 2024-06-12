package avalon.usuarios.data;

import avalon.usuarios.model.pojo.*;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioAseguradoraRepository extends JpaRepository<UsuarioAseguradora, Long> {

    UsuarioAseguradora findByAseguradoraAndUsuario(Aseguradora aseguradora, Usuario usuario);
    List<UsuarioAseguradora> findByUsuarioAndEstado(Usuario usuario, String estado);

    @Query("SELECT usu_asg FROM UsuarioAseguradora usu_asg WHERE usu_asg.estado = :estado AND usu_asg.aseguradora = :aseguradora AND usu_asg.usuario.rol = :rol")
    List<UsuarioAseguradora> findAllByAseguradoraAndEstadoAndRol(@Param("estado") String estado, @Param("aseguradora") Aseguradora aseguradora, @Param("rol") Rol rol);

    @Modifying
    @Transactional
    @Query("UPDATE UsuarioAseguradora ua SET ua.estado = 'I' WHERE ua.usuario = :usuario")
    void deleteAllByUsuarioAseguradorByUsuario(Usuario usuario);


}
