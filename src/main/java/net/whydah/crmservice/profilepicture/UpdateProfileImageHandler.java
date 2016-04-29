package net.whydah.crmservice.profilepicture;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.whydah.crmservice.profilepicture.model.ProfileImage;
import net.whydah.crmservice.security.Authentication;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.MediaType;
import ratpack.http.Request;
import ratpack.http.TypedData;

import java.sql.SQLIntegrityConstraintViolationException;

import static ratpack.jackson.Jackson.fromJson;

@Singleton
public class UpdateProfileImageHandler implements Handler {

    private final ProfileImageRepository repository;

    @Inject
    public UpdateProfileImageHandler(ProfileImageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        String customerRef = ctx.getPathTokens().get("customerRef");

        if ("useradmin".equalsIgnoreCase(Authentication.getAuthenticatedUser().getUid().toString())) {
        } else if (customerRef == null || !customerRef.equals(Authentication.getAuthenticatedUser().getPersonRef())) {
            ctx.clientError(401);
        }

        Request request = ctx.getRequest();

        MediaType contentType = request.getContentType();
        Promise<TypedData> bodyPromise = request.getBody();

        bodyPromise.then(data -> {
            Blocking.get(() -> repository.updateProfileImage(customerRef, new ProfileImage(data.getBytes(), contentType.getType()))).then(affectedRows -> {
                if (affectedRows == 1) {
                    ctx.redirect(202, "image"); //Accepted
                } else {
                    ctx.clientError(404); //Not found
                }
            });
        });
    }
}
