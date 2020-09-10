/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnu.no.oblig1h2v2.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import net.coobird.thumbnailator.Thumbnails;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;


/**
 *
 * @author eskil
 */
@Path("fant")
@Stateless
@DeclareRoles({Group.USER})
public class FantService {
    @Inject
    AuthenticationService authService;
    
    @Inject
    MailService mailService;
    
    @Context
    SecurityContext sc;
    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    @ConfigProperty(name = "photo.storage.path",defaultValue = "fantphotos")
    String photoPath;
    
    @GET
    @Path("users")
    @RolesAllowed({Group.USER})
    public List<User> getAllUsers(){
        return em.createNamedQuery(User.FIND_ALL_USERS).getResultList();
    }
    @GET
    @Path("items")
    @RolesAllowed({Group.USER /*Group.AMIN?*/})
    public List<Item> getItems(){
        return em.createNamedQuery(Item.FIND_ALL_ITEMS,Item.class).getResultList();
    }
    
    
    @PUT
    @Path("purchase")
    @RolesAllowed({Group.USER})
    public Response purchase(@QueryParam("itemid")Long itemid){
        Item item = em.find(Item.class, itemid);
        User customer = authService.getCurrentUser();
        if(item != null){
            if(item.getCustomer() == null){
                item.setCustomer(customer);
                
                String purchaseId = UUID.randomUUID().toString();
                Purchases purchase = new Purchases(purchaseId, customer, item);
                em.merge(purchase);
                
                mailService.sendEmail(item.getCustomer().getEmail(), "FANT - Item sold","Your item was sold" );
                return Response.ok().build();
                
                
                
            }
        }
        return Response.notModified().build();
    }
    
    @DELETE
    @Path("delete")
    @RolesAllowed({Group.USER})
    public Response deleteItem(@QueryParam("itemid") long itemid){
        Item item = em.find(Item.class, itemid);
        User user = this.getCurrentUser();
        if(item != null){
            if(item.getSeller().getUserid().equals(user.getUserid())){
                em.remove(item);
                return Response.ok(item).build();
            }
        }
        return Response.notModified().build();
    }
     private String getPhotoPath() {
        return photoPath;
    }
     
     //TODO: Fix adding of photos
     
    @POST
    @Path("add")
    //@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Group.USER})
    public Response addItem(@FormParam("title")String title, 
                            @FormParam ("description")String description, 
                            @FormParam("price")String price){
        Item item;
//        try{
            User user = em.find(User.class, sc.getUserPrincipal().getName());
            item = new Item(title,description,user,price);
//            List<FormDataBodyPart> images = multiPart.getFields("image");
//            if(images != null){
//                for(FormDataBodyPart part : images){
//                    InputStream is = part.getEntityAs(InputStream.class);
//                    ContentDisposition meta = part.getContentDisposition();
//                    
//                    String pid = UUID.randomUUID().toString();
//                    Files.copy(is, Paths.get(getPhotoPath(), pid));
//                    
//                    MediaObject photo = new MediaObject(pid, user,meta.getFileName(),meta.getSize(),meta.getType());
//                    em.persist(photo);
//                    item.addPhoto(photo);
//                }
//            }
            em.persist(item);
//        } catch(IOException ex){
//            Logger.getLogger(FantService.class.getName()).log(Level.SEVERE,null,ex);
//            return Response.serverError().build();
//        }
        return Response.ok(item).build();
        
    
    }
    
    @GET
    @Path("image/{name}")
    @Produces("image/jpeg")
    public Response getImage(@PathParam("name") String name, 
                             @QueryParam("width") int width) {
        if(em.find(MediaObject.class, name) != null) {
            StreamingOutput result = (OutputStream os) -> {
                java.nio.file.Path image = Paths.get(getPhotoPath(),name);
                if(width == 0) {
                    Files.copy(image, os);
                    os.flush();
                } else {
                    Thumbnails.of(image.toFile())
                              .size(width, width)
                              .outputFormat("jpeg")
                              .toOutputStream(os);
                }
            };

            // Ask the browser to cache the image for 24 hours
            CacheControl cc = new CacheControl();
            cc.setMaxAge(86400);
            cc.setPrivate(true);

            return Response.ok(result).cacheControl(cc).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }    
    private User getCurrentUser(){
        return em.find(User.class, sc.getUserPrincipal().getName());
    }
    
}
