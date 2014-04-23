/*****************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent
 * systems in compliance with the FIPA specifications. Copyright (C) 2000 CSELT
 * S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *****************************************************************/

package it.unibo.sd.jade.examples.bookTrading;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Adapted from Giovanni Caire's Book Trading example in examples.bookTrading
 * within JADE distribution. This is the buyer agent, showing how to query JADE
 * DF in order to look for a desired service.
 * 
 * @author s.mariani@unibo.it
 */
public class BookBuyerAgent extends Agent {

    /*
     * Behaviour sending the Call for Proposals to agents previously found.
     */
    private class CFPSender extends OneShotBehaviour {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            final ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            for (final AID sellerAgent : BookBuyerAgent.this.sellerAgents) {
                BookBuyerAgent.this.log("Sending CFP for book '"
                        + BookBuyerAgent.this.targetBookTitle + "' to agent '"
                        + sellerAgent.getName() + "'...");
                cfp.addReceiver(sellerAgent);
            }
            cfp.setContent(BookBuyerAgent.this.targetBookTitle);
            cfp.setConversationId("book-trade");
            /*
             * We add a (approximatively) unique value for identification
             * purpose: the receivers will reply using this field.
             */
            cfp.setReplyWith("cfp" + System.currentTimeMillis());
            this.myAgent.send(cfp);
            /*
             * We only listen to replies carrying the specified unique value.
             */
            BookBuyerAgent.this.mt =
                    MessageTemplate.and(
                            MessageTemplate.MatchConversationId("book-trade"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
        }

    }

    /*
     * Behaviour waiting for the purchase confirmation.
     */
    private class ConfirmationReceiver extends Behaviour {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;
        /*
         * For termination.
         */
        private boolean flag = false;

        @Override
        public void action() {
            BookBuyerAgent.this
                    .log("Waiting for purchase confirmation messages...");
            final ACLMessage reply =
                    this.myAgent.receive(BookBuyerAgent.this.mt);
            if (reply != null) {
                this.flag = true;
                BookBuyerAgent.this.log("Received confirmation '"
                        + reply.getReplyWith() + "' from '"
                        + reply.getSender().getName() + "'.");
                if (reply.getPerformative() == ACLMessage.INFORM) {
                    /*
                     * In case of positive answer, purchase succeeded.
                     */
                    BookBuyerAgent.this.log("Book '"
                            + BookBuyerAgent.this.targetBookTitle
                            + "' has been successfully purchased"
                            + " from agent '" + reply.getSender().getName()
                            + "'.");
                } else {
                    /*
                     * Otherwise, purchase failed.
                     */
                    BookBuyerAgent.this.log("Book '"
                            + BookBuyerAgent.this.targetBookTitle
                            + "' has been already sold :(");
                }
            } else {
                this.block();
            }
        }

        /*
         * Upon reception of a confirmation/failure message we can terminate.
         */
        @Override
        public boolean done() {
            return this.flag;
        }

    }

    /*
     * Terminating state if no proposals have been received, hence no purchase
     * attempt has to be done.
     */
    private class NoProposals extends OneShotBehaviour {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            BookBuyerAgent.this
                    .log("No proposals received, trying another book in 10 seconds...");
        }

    }

    /*
     * Behaviour collecting the Proposals (possibly) sent by advertising agents.
     */
    private class ProposalsCollector extends Behaviour {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            /*
             * Use previous message template to collect all proposals/refusals
             * from previously found seller agents.
             */
            BookBuyerAgent.this.log("Waiting for proposals...");
            final ACLMessage reply =
                    this.myAgent.receive(BookBuyerAgent.this.mt);
            if (reply != null) {
                BookBuyerAgent.this.log("Received proposal '"
                        + reply.getReplyWith() + "' from '"
                        + reply.getSender().getName() + "'.");
                if (reply.getPerformative() == ACLMessage.PROPOSE) {
                    /*
                     * In case of a positive answer, update current best seller
                     * based upon proposed book price.
                     */
                    final float price = Float.parseFloat(reply.getContent());
                    if ((BookBuyerAgent.this.bestSeller == null)
                            || (price < BookBuyerAgent.this.bestPrice)) {
                        BookBuyerAgent.this.bestPrice = price;
                        BookBuyerAgent.this.bestSeller = reply.getSender();
                    }
                }
                /*
                 * In case of any non-positive answer, do nothing. In any case,
                 * increase replies counter.
                 */
                BookBuyerAgent.this.repliesCnt++;
            } else {
                this.block();
            }

        }

        /*
         * Upon collection of all the responses, this behaviour can be removed.
         */
        @Override
        public boolean done() {
            return BookBuyerAgent.this.repliesCnt >= BookBuyerAgent.this.sellerAgents.length;
        }

        /*
         * If no one had our desired book we should not try to purchase it.
         */
        @Override
        public int onEnd() {
            if (BookBuyerAgent.this.bestSeller != null) {
                return 0;
            }
            return 1;
        }

    }

    /*
     * Behaviour performing the attempt to buy the searched book.
     */
    private class Purchaser extends OneShotBehaviour {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            /*
             * Send the purchase order to the seller who proposed the best
             * offer.
             */
            final ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            order.addReceiver(BookBuyerAgent.this.bestSeller);
            order.setContent(BookBuyerAgent.this.targetBookTitle);
            order.setConversationId("book-trade");
            /*
             * Again, (the approximatively) unique value for identification
             * purpose.
             */
            order.setReplyWith("order" + System.currentTimeMillis());
            BookBuyerAgent.this.log("Sending purchase order for book '"
                    + BookBuyerAgent.this.targetBookTitle + "' to agent '"
                    + BookBuyerAgent.this.bestSeller.getName() + "'...");
            this.myAgent.send(order);
            /*
             * We only listen to replies carrying the specified unique value.
             */
            BookBuyerAgent.this.mt =
                    MessageTemplate.and(MessageTemplate
                            .MatchConversationId("book-trade"), MessageTemplate
                            .MatchInReplyTo(order.getReplyWith()));
        }

    }

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    /*
     * The best offered price.
     */
    private float bestPrice;
    /*
     * The agent who provides the best offer.
     */
    private AID bestSeller;

    /*
     * Message template to be used during conversations.
     */
    private MessageTemplate mt;

    /*
     * Overall number of book trading attempts, used for termination.
     */
    private int overallAttempts = 0;

    /*
     * We should keep track of received replies.
     */
    private int repliesCnt = 0;

    /*
     * The list of discovered seller agents.
     */
    private AID[] sellerAgents;

    /*
     * The title of the book to buy.
     */
    private String targetBookTitle;

    @Override
    protected void setup() {

        this.log("I'm started.");
        /*
         * Periodic behaviour performing random book requests.
         */
        this.addBehaviour(new TickerBehaviour(this, 10000) {

            /** serialVersionUID **/
            private static final long serialVersionUID = 1L;

            @Override
            public int onEnd() {
                BookBuyerAgent.this.log("Terminating...");
                this.myAgent.doDelete();
                return super.onEnd();
            }

            @Override
            protected void onTick() {

                /*
                 * Termination condition.
                 */
                if (BookBuyerAgent.this.overallAttempts == 30) {
                    this.stop();
                }
                /*
                 * Randomly draw the book to buy from .catalog file.
                 */
                BookBuyerAgent.this.targetBookTitle =
                        BookBuyerAgent.this.bootBookTitle();
                /*
                 * Resets fields and increase attempts counter.
                 */
                BookBuyerAgent.this.bestSeller = null;
                BookBuyerAgent.this.bestPrice = 0f;
                BookBuyerAgent.this.repliesCnt = 0;
                BookBuyerAgent.this.overallAttempts++;
                BookBuyerAgent.this.log("Trying to buy '"
                        + BookBuyerAgent.this.targetBookTitle + "'...");
                /*
                 * 1- Create the agent description template.
                 */
                final DFAgentDescription template = new DFAgentDescription();
                /*
                 * 2- Create the service description template.
                 */
                final ServiceDescription sd = new ServiceDescription();
                /*
                 * 3- Fill its fields you look for.
                 */
                sd.setType("book-selling");
                /*
                 * 4- Add the service template to the agent template.
                 */
                template.addServices(sd);
                /*
                 * 5- Setup your preferred search constraints.
                 */
                final SearchConstraints all = new SearchConstraints();
                all.setMaxResults(new Long(-1));
                DFAgentDescription[] result = null;
                try {
                    /*
                     * 6- Query the DF about the service you look for.
                     */
                    BookBuyerAgent.this.log("Searching '" + sd.getType()
                            + "' service in the default DF...");
                    result = DFService.search(this.myAgent, template, all);
                    BookBuyerAgent.this.sellerAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        /*
                         * 7- Collect found service providers' AIDs.
                         */
                        BookBuyerAgent.this.sellerAgents[i] =
                                result[i].getName();
                        BookBuyerAgent.this.log("Agent '"
                                + BookBuyerAgent.this.sellerAgents[i].getName()
                                + "' found.");
                    }
                } catch (final FIPAException fe) {
                    fe.printStackTrace();
                }

                /*
                 * If we found at least one agent offering the desired service,
                 * we try to buy the book using a custom FSM-like behaviour.
                 */
                if (result.length != 0) {
                    final FSMBehaviour fsm = new FSMBehaviour(this.myAgent);
                    this.configureFSM(fsm);
                    this.myAgent.addBehaviour(fsm);
                } else {
                    BookBuyerAgent.this
                            .log("No suitable services found, retrying in 10 seconds...");
                }

            }

            private void configureFSM(final FSMBehaviour fsm) {
                fsm.registerFirstState(new CFPSender(), "CFPState");
                fsm.registerState(new ProposalsCollector(), "ProposalsState");
                fsm.registerState(new Purchaser(), "PurchaseState");
                fsm.registerLastState(new ConfirmationReceiver(),
                        "ConfirmationState");
                fsm.registerLastState(new NoProposals(), "NoProposalsState");
                fsm.registerDefaultTransition("CFPState", "ProposalsState");
                fsm.registerTransition("ProposalsState", "PurchaseState", 0);
                fsm.registerTransition("ProposalsState", "NoProposalsState", 1);
                fsm.registerDefaultTransition("PurchaseState",
                        "ConfirmationState");
            }

        });

    }

    @Override
    protected void takeDown() {
        this.log("I'm done.");
    }

    /*
     * Just draw a random book title from an input file.
     */
    private String bootBookTitle() {
        try {
            final BufferedReader br =
                    new BufferedReader(new FileReader(
                            "bin/ds/lab/jade/bookTrading/books.cat"));
            String line;
            StringTokenizer st;
            final LinkedList<String> titles = new LinkedList<String>();
            line = br.readLine();
            while (line != null) {
                st = new StringTokenizer(line, ";");
                titles.add(st.nextToken());
                line = br.readLine();
            }
            br.close();
            return titles.get((int) Math.round(Math.random()
                    * (titles.size() - 1)));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
            this.doDelete();
        } catch (final IOException e) {
            e.printStackTrace();
            this.doDelete();
        }
        return "";
    }

    private void log(final String msg) {
        System.out.println("[" + this.getName() + "]: " + msg);
    }

}