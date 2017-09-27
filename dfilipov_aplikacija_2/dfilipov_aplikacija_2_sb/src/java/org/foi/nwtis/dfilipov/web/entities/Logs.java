package org.foi.nwtis.dfilipov.web.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "LOGS")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Logs.findAll", query = "SELECT l FROM Logs l"),
	@NamedQuery(name = "Logs.findById", query = "SELECT l FROM Logs l WHERE l.id = :id"),
	@NamedQuery(name = "Logs.findByRequest", query = "SELECT l FROM Logs l WHERE l.request = :request"),
	@NamedQuery(name = "Logs.findByLength", query = "SELECT l FROM Logs l WHERE l.length = :length"),
	@NamedQuery(name = "Logs.findByDatetime", query = "SELECT l FROM Logs l WHERE l.datetime = :datetime")})
public class Logs implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
	private Integer id;
	
	@Size(max = 256)
    @Column(name = "REQUEST")
	private String request;
	
	@Column(name = "LENGTH")
	private Integer length;
	@Column(name = "DATETIME")
    
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetime;
	
	@JoinColumn(name = "USER_", referencedColumnName = "ID")
    @ManyToOne
	private Users user;

	public Logs()
	{
	}

	public Logs(Integer id)
	{
		this.id = id;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getRequest()
	{
		return request;
	}

	public void setRequest(String request)
	{
		this.request = request;
	}

	public Integer getLength()
	{
		return length;
	}

	public void setLength(Integer length)
	{
		this.length = length;
	}

	public Date getDatetime()
	{
		return datetime;
	}

	public void setDatetime(Date datetime)
	{
		this.datetime = datetime;
	}

	public Users getUser()
	{
		return user;
	}

	public void setUser(Users user)
	{
		this.user = user;
	}

	@Override
	public int hashCode()
	{
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof Logs)) {
			return false;
		}
		Logs other = (Logs) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "org.foi.nwtis.dfilipov.web.entities.Logs[ id=" + id + " ]";
	}
	
}
