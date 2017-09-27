package org.foi.nwtis.dfilipov.web.entities;

import java.io.Serializable;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "USERS")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
	@NamedQuery(name = "Users.findById", query = "SELECT u FROM Users u WHERE u.id = :id"),
	@NamedQuery(name = "Users.findByUsername", query = "SELECT u FROM Users u WHERE u.username = :username"),
	@NamedQuery(name = "Users.findBySurname", query = "SELECT u FROM Users u WHERE u.surname = :surname"),
	@NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password"),
	@NamedQuery(name = "Users.findByEmail", query = "SELECT u FROM Users u WHERE u.email = :email"),
	@NamedQuery(name = "Users.findByIsWaiting", query = "SELECT u FROM Users u WHERE u.isWaiting = :isWaiting"),
	@NamedQuery(name = "Users.findByIsRejected", query = "SELECT u FROM Users u WHERE u.isRejected = :isRejected")})
public class Users implements Serializable
{
	@OneToMany(mappedBy = "user")
	private List<Logs> logsList;
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
	private Integer id;
	
	@Size(max = 30)
    @Column(name = "USERNAME")
	private String username;
	
	@Size(max = 30)
    @Column(name = "SURNAME")
	private String surname;
	
	@Size(max = 100)
    @Column(name = "PASSWORD")
	private String password;
	// @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
	
	@Size(max = 50)
    @Column(name = "EMAIL")
	private String email;
	
	@Column(name = "IS_WAITING")
	private Boolean isWaiting;
	
	@Column(name = "IS_REJECTED")
	private Boolean isRejected;
	
	@JoinColumn(name = "ROLE", referencedColumnName = "ID")
    @ManyToOne
	private Roles role;

	public Users()
	{
	}

	public Users(Integer id)
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

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public Boolean getIsWaiting()
	{
		return isWaiting;
	}

	public void setIsWaiting(Boolean isWaiting)
	{
		this.isWaiting = isWaiting;
	}

	public Boolean getIsRejected()
	{
		return isRejected;
	}

	public void setIsRejected(Boolean isRejected)
	{
		this.isRejected = isRejected;
	}

	public Roles getRole()
	{
		return role;
	}

	public void setRole(Roles role)
	{
		this.role = role;
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
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Users)) {
			return false;
		}
		Users other = (Users) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "org.foi.nwtis.dfilipov.web.entities.Users[ id=" + id + " ]";
	}

	@XmlTransient
	public List<Logs> getLogsList()
	{
		return logsList;
	}

	public void setLogsList(List<Logs> logsList)
	{
		this.logsList = logsList;
	}
	
}
