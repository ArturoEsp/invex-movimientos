package com.invex.dtos.movimientos;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMovimientosDto implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<MovimientosDto> movimientos;

}
