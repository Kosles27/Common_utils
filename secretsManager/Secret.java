package secretsManager;

import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Holds an Akeyless Secret Object
 */
public class Secret {

    private String fullSecretPath;
    private String password;

    public Secret(String fullSecretPath,String secretValue){
        this.fullSecretPath = fullSecretPath;
        this.password = secretValue;
    }

    public String getFullSecretPath() {
        return fullSecretPath;
    }

    public Secret setFullSecretPath(String fullSecretPath) {
        this.fullSecretPath = fullSecretPath;
        return this;
    }

    /**
     *
     * extract the username from the fullSecretPath (last part after the "/")
     * @return username
     * @author sela.zvika
     * @since 02.19.23
     */
    public String getUsername() {
        Path p = Paths.get(fullSecretPath);
        return p.getFileName().toString();

    }

    public String getPassword() {
        return password;
    }

    public Secret setPassword(String password) {
        this.password = password;
        return this;
    }



}
