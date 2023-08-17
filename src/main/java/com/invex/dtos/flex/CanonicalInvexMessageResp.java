package com.invex.dtos.flex;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanonicalInvexMessageResp implements Serializable{
    private static final long serialVersionUID = 1L;
	private HeaderFlex header;
    private BodyFlex body;
    
}
