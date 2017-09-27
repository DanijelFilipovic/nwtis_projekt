package org.foi.nwtis.dfilipov.web.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "ROLES")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Roles.findAll", query = "SELECT r FROM Roles r"),
	@NamedQuery(name = "Roles.findById", query = "SELECT r FROM Roles r WHERE r.id = :id"),
	@NamedQuery(name = "Roles.findByName", query = "SELECT r FROM Roles r WHERE r.name = :name")})
public class Roles implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
	private Integer id;
	
	@Size(max = 5)
    @Column(name = "NAME")
	private String name;
	
	@OneToMany(mappedBy = "role")
	private List<Users> usersList;

	public Roles()
	{
	}

	public Roles(Integer id)
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@XmlTransient
	public List<Users> getUsersList()
	{
		return usersList;
	}

	public void setUsersList(List<Users> usersList)
	{
		this.usersList = usersList;
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
		if (!(object instanceof Roles)) {
			return false;
		}
		Roles other = (Roles) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "org.foi.nwtis.dfilipov.web.entities.Roles[ id=" + id + " ]";
	}
	
}
