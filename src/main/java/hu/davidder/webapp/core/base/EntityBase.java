package hu.davidder.webapp.core.base;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

@MappedSuperclass
public class EntityBase implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    protected Long id;

    @JsonIgnore
    protected boolean deleted = false;

    @JsonIgnore
    protected boolean status = true;

    @Column(name = "CREATE_DATE", nullable = false)
    @JsonIgnore
    protected ZonedDateTime createDate = ZonedDateTime.now(ZoneId.of("UTC"));

    @Column(name = "MODIFY_DATE", nullable = true)
    @JsonIgnore
    protected ZonedDateTime modifyDate;

    @Column(name = "DELETE_DATE", nullable = true)
    @JsonIgnore
    protected ZonedDateTime deleteDate;

    @Column(name = "STATUS_MODIFY_DATE", nullable = true)
    @JsonIgnore
    protected ZonedDateTime statusModifyDate;
    
    @Version
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleteDate = deleted ? ZonedDateTime.now() : null;
        this.modifyDate = ZonedDateTime.now();
        this.deleted = deleted;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.statusModifyDate = status ? ZonedDateTime.now() : null;
        this.modifyDate = ZonedDateTime.now();
        this.status = status;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public ZonedDateTime getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(ZonedDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    public ZonedDateTime getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(ZonedDateTime deleteDate) {
        this.deleteDate = deleteDate;
    }

	public ZonedDateTime getStatusModifyDate() {
		return statusModifyDate;
	}

	public void setStatusModifyDate(ZonedDateTime statusModifyDate) {
		this.statusModifyDate = statusModifyDate;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
    
    
}