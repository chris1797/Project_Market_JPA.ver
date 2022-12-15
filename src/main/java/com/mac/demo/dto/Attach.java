package com.mac.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="ATTACH_TB")
public class Attach {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long att_num;  // index
	private long pcode; // 부모코드(게시판 num)
	private String user_id; // 유저 ID
	private String filename; // 파일 이름
	
	@Column(length=500)
	private String filepath; // 파일 저장된 경로
	
	@Temporal(TemporalType.DATE)
	private Date wdate; // 파일 저장 날짜
	
	@Transient
	private List<Attach> attList; // 첨부파일명 리스트

	@Builder
	public Attach(Long pcode, String user_id, String filename, String filepath, Date wdate, List<Attach> attList) {
		this.pcode = pcode;
		this.user_id = user_id;
		this.filename = filename;
		this.filepath = filepath;
		this.wdate = wdate;
		this.attList = attList;
	}

	public Attach toEntity() {
		return Attach.builder()
				.pcode(pcode)
				.user_id(user_id)
				.filename(filename)
				.filepath(filepath)
				.wdate(wdate)
				.attList(attList)
				.build();
	}
}