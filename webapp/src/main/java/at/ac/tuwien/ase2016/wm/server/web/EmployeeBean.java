package at.ac.tuwien.ase2016.wm.server.web;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class EmployeeBean implements Serializable {

    public String logout() {
        return "/index.xhtml";
    }
}
