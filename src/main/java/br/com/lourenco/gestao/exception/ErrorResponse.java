package br.com.lourenco.gestao.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoErro> campos
) {

    public record CampoErro(String campo, String mensagem) {
    }

    public static ErrorResponse de(HttpStatus status, String mensagem,
                                   String caminho, List<CampoErro> campos) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                caminho,
                campos);
    }
}
