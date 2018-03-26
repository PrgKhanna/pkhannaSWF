package com.swf.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "engineer_shift")
@DynamicInsert
@DynamicUpdate
public class EngineerShift implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "engineer_id")
	private Integer engineerId;

	@Column(name = "shift")
	private Byte shift;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Temporal(TemporalType.DATE)
	@Column(name = "date")
	private Date date;

	@PrePersist
	protected void onCreate() {
		createdOn = new Date();
		updatedOn = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedOn = new Date();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEngineerId() {
		return engineerId;
	}

	public void setEngineerId(Integer engineerId) {
		this.engineerId = engineerId;
	}

	public Byte getShift() {
		return shift;
	}

	public void setShift(Byte shift) {
		this.shift = shift;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "EngineerShift [id=" + id + ", engineerId=" + engineerId + ", shift=" + shift + ", createdOn="
				+ createdOn + ", updatedOn=" + updatedOn + ", date=" + date + "]";
	}

}
