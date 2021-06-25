package mancala.api;

import java.io.IOException;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import mancala.api.models.*;
import mancala.domain.MancalaException;
import mancala.domain.MancalaImpl;

// public class StartMancala {
//     @POST
//     @Consumes(MediaType.APPLICATION_JSON)
//     @Produces(MediaType.APPLICATION_JSON)
//     public Response initialize(
//             @Context HttpServletRequest request,
//             PlayerInput players) {
//         // var mancala = new MancalaImpl();
//         var mancala = new MancalaImpl(new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13});
//         String namePlayer1 = players.getNameplayer1();
//         String namePlayer2 = players.getNameplayer2();
//
//         HttpSession session = request.getSession(true);
//         session.setAttribute("mancala", mancala);
//         session.setAttribute("player1", namePlayer1);
//         session.setAttribute("player2", namePlayer2);
//
//         var output = new Mancala(mancala, namePlayer1, namePlayer2);
//         return Response.status(200).entity(output).build();
//     }
// }
@Path("/play")
public class PlayMancala {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response initialize(@Context HttpServletRequest request, PlayInfo playInfo) {

        HttpSession session = request.getSession();

        MancalaImpl mancala = (MancalaImpl) session.getAttribute("mancala");
        String namePlayer1 = (String) session.getAttribute("player1");
        String namePlayer2 = (String) session.getAttribute("player2");
        try {
            mancala.playPit(playInfo.getIndex());
        } catch (MancalaException e) {
            return Response.status(403).build();
        }
        var output = new Mancala(mancala, namePlayer1, namePlayer2);

        return Response.status(200).entity(output).build();
    }
}
