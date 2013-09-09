/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002 aliCE team at
 * deis.unibo.it This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.tucson.introspection.tools;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.introspection.InspectorContext;
import alice.tucson.introspection.InspectorProtocol;

/**
 * 
 * @author Unknown...
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public class InspectorGUI extends javax.swing.JFrame {

    private static final long serialVersionUID = -3765811664087552414L;

    /**
     * 
     * 
     * @param args
     *            the arguments to launch the inspector
     * 
     */
    public static void main(final String[] args) {

        System.out.println("[Inspector]: Booting...");

        if (alice.util.Tools.isOpt(args, "-?")
                || alice.util.Tools.isOpt(args, "-help")) {
            System.out.println("Argument Template: ");
            System.out
                    .println("{-tname tuple centre name} {-netid ip address} {-portno listening port number} {-aid agent identifier} {-?}");
            System.exit(0);
        }

        String stAid = null;
        if (alice.util.Tools.isOpt(args, "-aid")) {
            stAid = alice.util.Tools.getOpt(args, "-aid");
        } else {
            stAid = "inspector" + System.currentTimeMillis();
        }
        String tcname = "";
        if (alice.util.Tools.isOpt(args, "-tcname")) {
            tcname = alice.util.Tools.getOpt(args, "-tcname");
        }
        String netid = "";
        if (alice.util.Tools.isOpt(args, "-netid")) {
            netid = alice.util.Tools.getOpt(args, "-netid");
        }
        String port = "";
        if (alice.util.Tools.isOpt(args, "-portno")) {
            port = alice.util.Tools.getOpt(args, "-portno");
        }

        TucsonAgentId aid = null;
        TucsonTupleCentreId tid = null;
        try {
            aid = new TucsonAgentId(stAid);
            tid = new TucsonTupleCentreId(tcname, netid, port);
            System.out.println("[Inspector]: Inspector Agent Identifier: "
                    + stAid);
            System.out.println("[Inspector]: Tuple Centre Identifier: " + tid);
        } catch (final TucsonInvalidAgentIdException e) {
            System.err.println("[Inspector]: failure: " + e);
            System.exit(-1);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            System.out
                    .println("[Inspector]: Please input an admissible tuplecentre "
                            + "name from the GUI...");
        }

        InspectorGUI form = null;
        if (tid != null) {
            form = new InspectorGUI(aid, tid);
        } else {
            form = new InspectorGUI(aid);
        }
        synchronized (form.exit) {
            try {
                form.exit.wait();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[Inspector]: I quit, see you next time :)");
        System.exit(0);

    }

    /**
     * Package-visible reference to the Inspector core machinery.
     */
    protected InspectorCore agent;
    /**
     * Package-visible reference to the Inspector session.
     */
    protected InspectorProtocol protocol = new InspectorProtocol();
    /**
     * Package-visible reference to the inspected tuplecentre.
     */
    protected TucsonTupleCentreId tid;
    private final TucsonAgentId aid;
    private javax.swing.JButton buttonInspect;
    private javax.swing.JButton buttonNextStep;
    private javax.swing.JCheckBox checkStepExecution;
    private javax.swing.JPanel chooseTC;
    private InspectorContext context;
    private javax.swing.JTabbedPane controlPanel;

    private final Object exit = new Object();
    private javax.swing.JPanel imgPanel;
    private javax.swing.JTextField inputName;
    private javax.swing.JTextField inputNode;
    private javax.swing.JTextField inputPort;
    private boolean isSessionOpen;
    private EventViewer pendingQueryForm;
    private ReactionViewer reactionForm;
    private EditSpec specForm;
    private javax.swing.JTextField stateBar;
    private TupleViewer tupleForm;

    /**
     * Called when no default tuplecentre to monitor is given.
     * 
     * @param id
     *            the name of the Inspector agent.
     */
    public InspectorGUI(final TucsonAgentId id) {
        super();
        this.initComponents();
        this.aid = id;
        this.disableControlPanel();
        this.isSessionOpen = false;
        this.setVisible(true);
    }

    /**
     * Called when a default tuplecentre to inspect is given.
     * 
     * @param id
     *            the name of the Inspector agent.
     * @param tc
     *            the fullname of the tuplecentre to inspect.
     */
    public InspectorGUI(final TucsonAgentId id, final TucsonTupleCentreId tc) {
        this(id);
        this.tid = tc;
        this.inputName.setText(tc.getName());
        this.inputNode.setText(alice.util.Tools.removeApices(tc.getNode()));
        this.inputPort.setText(alice.util.Tools.removeApices(String
                .valueOf(this.tid.getPort())));
        this.buttonInspectActionPerformed();
    }

    public void onEventViewerExit() {
        this.protocol
                .setPendingQueryObservType(InspectorProtocol.NO_OBSERVATION);
        try {
            this.context.setProtocol(this.protocol);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    protected EventViewer getQueryForm() {
        return this.pendingQueryForm;
    }

    protected ReactionViewer getReactionForm() {
        return this.reactionForm;
    }

    /*
     * Forms getter.
     */
    protected TupleViewer getTupleForm() {
        return this.tupleForm;
    }

    protected void onReactionViewerExit() {
        this.protocol.setReactionsObservType(InspectorProtocol.NO_OBSERVATION);
        try {
            this.context.setProtocol(this.protocol);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 'On-exit' callbacks.
     */
    protected void onTupleViewerExit() {
        this.protocol.setTsetObservType(InspectorProtocol.NO_OBSERVATION);
        try {
            this.context.setProtocol(this.protocol);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void buttonInspectActionPerformed() {

        if (!this.isSessionOpen) {

            try {

                final String name = this.inputName.getText();
                final String address = this.inputNode.getText();
                final String port = this.inputPort.getText();

                this.tid = new TucsonTupleCentreId(name, address, port);
                this.agent = new InspectorCore(this, this.aid, this.tid);
                this.context = this.agent.getContext();
                this.agent.start();

                this.tupleForm = new TupleViewer(this);
                this.pendingQueryForm = new EventViewer(this);
                this.reactionForm = new ReactionViewer(this);
                this.specForm = new EditSpec(this.tid);
                this.inputName.setText(this.tid.getName());
                this.inputNode.setText(alice.util.Tools.removeApices(this.tid
                        .getNode()));
                this.inputPort.setText(alice.util.Tools.removeApices(String
                        .valueOf(this.tid.getPort())));
                this.checkStepExecution.setSelected(false);
                this.buttonNextStep.setEnabled(false);
                this.inputName.setEditable(false);
                this.inputNode.setEditable(false);
                this.inputPort.setEditable(false);

                this.buttonInspect.setText("Quit");
                this.enableControlPanel();
                this.isSessionOpen = true;
                this.stateBar.setText("Inspector Session Opened.");

            } catch (final TucsonInvalidTupleCentreIdException e) {
                this.stateBar.setText("Operation Failed: " + e);
                e.printStackTrace();
            }

        } else {

            this.specForm.exit();
            this.agent.quit();
            this.buttonInspect.setText("Inspect!");
            this.disableControlPanel();
            this.inputName.setEditable(true);
            this.inputNode.setEditable(true);
            this.inputPort.setEditable(true);
            this.isSessionOpen = false;
            this.stateBar.setText("Inspector Session Closed.");

        }

    }

    /**
     * Since the tuplecentre to inspect is not chosen, the control panel is not
     * showed.
     */
    private void disableControlPanel() {
        this.setSize(380, 200);
        this.validate();
        this.chooseTC.setBorder(new javax.swing.border.TitledBorder(null,
                " Input tuplecentre to inspect: ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Arial", 0, 12)));
        this.controlPanel.setVisible(false);
        this.imgPanel.setVisible(true);
    }

    /**
     * Once tuplecentre to inspect is chosen, the control panel appears.
     */
    private void enableControlPanel() {
        this.setSize(380, 300);
        this.validate();
        this.chooseTC.setBorder(new javax.swing.border.TitledBorder(null,
                " Now inspecting tuplecentre: ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Arial", 0, 12)));
        this.controlPanel.setVisible(true);
        this.imgPanel.setVisible(false);
    }

    /**
     * Close all open forms on exit (Inspector itself too).
     * 
     * @param evt
     *            closing window event.
     */
    private void exitForm() {
        if (this.isSessionOpen) {
            try {
                this.context.exit();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        if (this.tupleForm != null) {
            this.tupleForm.dispose();
        }
        if (this.pendingQueryForm != null) {
            this.pendingQueryForm.dispose();
        }
        if (this.reactionForm != null) {
            this.reactionForm.dispose();
        }
        if (this.specForm != null) {
            this.specForm.dispose();
        }
        this.dispose();
        synchronized (this.exit) {
            this.exit.notify();
        }
    }

    /**
     * GUI set up.
     */
    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        this.controlPanel = new javax.swing.JTabbedPane();
        final JPanel respectSetsPanel = new JPanel();
        final JButton tuplesBtn = new JButton();
        final JButton pendingBtn = new JButton();
        final JButton reactionsBtn = new JButton();
        final JButton specBtn = new JButton();
        this.checkStepExecution = new javax.swing.JCheckBox();
        this.buttonNextStep = new javax.swing.JButton();
        this.chooseTC = new javax.swing.JPanel();
        final JLabel tname = new JLabel();
        final JLabel netid = new JLabel();
        final JLabel portno = new JLabel();
        this.inputName = new javax.swing.JTextField();
        this.inputNode = new javax.swing.JTextField();
        this.inputPort = new javax.swing.JTextField();
        this.buttonInspect = new javax.swing.JButton();
        this.imgPanel = new javax.swing.JPanel();
        final JLabel jLabel1 = new JLabel();
        this.stateBar = new javax.swing.JTextField();

        this.getContentPane().setLayout(new java.awt.GridBagLayout());

        this.setTitle("TuCSoN Inspector");
        this.setResizable(false);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(final java.awt.event.WindowEvent evt) {
                InspectorGUI.this.exitForm();
            }
        });

        this.controlPanel.setFont(new java.awt.Font("Arial", 0, 11));
        this.controlPanel.setMaximumSize(new java.awt.Dimension(360, 120));
        this.controlPanel.setMinimumSize(new java.awt.Dimension(360, 120));
        this.controlPanel.setPreferredSize(new java.awt.Dimension(360, 120));

        respectSetsPanel.setLayout(new java.awt.GridBagLayout());
        respectSetsPanel
                .setToolTipText("Now inspecting TuCSoN tuplecentre sets");
        respectSetsPanel.setMinimumSize(new java.awt.Dimension(240, 100));
        respectSetsPanel.setPreferredSize(new java.awt.Dimension(240, 100));

        tuplesBtn.setFont(new java.awt.Font("Arial", 0, 11));
        tuplesBtn.setText("Tuple Space");
        tuplesBtn.setToolTipText("Inspect logic tuples set");
        tuplesBtn.setMaximumSize(new java.awt.Dimension(130, 25));
        tuplesBtn.setMinimumSize(new java.awt.Dimension(130, 25));
        tuplesBtn.setPreferredSize(new java.awt.Dimension(130, 25));
        tuplesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                InspectorGUI.this.tuplesBtnActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 30.0;
        gridBagConstraints.weighty = 100.0;
        respectSetsPanel.add(tuplesBtn, gridBagConstraints);

        pendingBtn.setFont(new java.awt.Font("Arial", 0, 11));
        pendingBtn.setText("Pending Ops");
        pendingBtn.setToolTipText("Inspect pending TuCSoN operations set");
        pendingBtn.setMaximumSize(new java.awt.Dimension(130, 25));
        pendingBtn.setMinimumSize(new java.awt.Dimension(130, 25));
        pendingBtn.setPreferredSize(new java.awt.Dimension(130, 25));
        pendingBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                InspectorGUI.this.pendingBtnActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 30.0;
        gridBagConstraints.weighty = 100.0;
        respectSetsPanel.add(pendingBtn, gridBagConstraints);

        reactionsBtn.setFont(new java.awt.Font("Arial", 0, 11));
        reactionsBtn.setText("ReSpecT Reactions");
        reactionsBtn.setToolTipText("Inspect triggered ReSpecT rections set");
        reactionsBtn.setMaximumSize(new java.awt.Dimension(130, 25));
        reactionsBtn.setMinimumSize(new java.awt.Dimension(130, 25));
        reactionsBtn.setPreferredSize(new java.awt.Dimension(130, 25));
        reactionsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                InspectorGUI.this.trigReactsBtnActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 30.0;
        gridBagConstraints.weighty = 100.0;
        respectSetsPanel.add(reactionsBtn, gridBagConstraints);

        specBtn.setFont(new java.awt.Font("Arial", 0, 11));
        specBtn.setText("Specification Space");
        specBtn.setToolTipText("Inspect ReSpecT specification tuples set");
        specBtn.setMaximumSize(new java.awt.Dimension(130, 25));
        specBtn.setMinimumSize(new java.awt.Dimension(130, 25));
        specBtn.setPreferredSize(new java.awt.Dimension(130, 25));
        specBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                InspectorGUI.this.specBtnActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 50.0;
        gridBagConstraints.weighty = 100.0;
        respectSetsPanel.add(specBtn, gridBagConstraints);

        this.controlPanel.addTab("Sets", null, respectSetsPanel,
                "Now inspecting TuCSoN tuplecentre sets");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.getContentPane().add(this.controlPanel, gridBagConstraints);
        this.chooseTC.setLayout(new java.awt.GridBagLayout());

        this.chooseTC.setBorder(new javax.swing.border.TitledBorder(null,
                " Input tuplecentre to inspect: ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Arial", 0, 12)));
        this.chooseTC.setMaximumSize(new java.awt.Dimension(360, 160));
        this.chooseTC.setMinimumSize(new java.awt.Dimension(360, 160));
        this.chooseTC.setPreferredSize(new java.awt.Dimension(360, 160));

        tname.setText("tname");
        tname.setMaximumSize(new java.awt.Dimension(60, 20));
        tname.setMinimumSize(new java.awt.Dimension(60, 20));
        tname.setPreferredSize(new java.awt.Dimension(60, 20));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        this.chooseTC.add(tname, gridBagConstraints);

        netid.setText("@netid");
        netid.setMaximumSize(new java.awt.Dimension(60, 20));
        netid.setMinimumSize(new java.awt.Dimension(60, 20));
        netid.setPreferredSize(new java.awt.Dimension(60, 20));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        this.chooseTC.add(netid, gridBagConstraints);

        portno.setText(":portno");
        portno.setMaximumSize(new java.awt.Dimension(60, 20));
        portno.setMinimumSize(new java.awt.Dimension(60, 20));
        portno.setPreferredSize(new java.awt.Dimension(60, 20));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        this.chooseTC.add(portno, gridBagConstraints);

        this.inputName.setText("default");
        this.inputName.setToolTipText("tuple centre name");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        this.chooseTC.add(this.inputName, gridBagConstraints);

        this.inputNode.setText("localhost");
        this.inputNode.setToolTipText("IP address of the TuCSoN Node");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        this.chooseTC.add(this.inputNode, gridBagConstraints);

        this.inputPort.setText("20504");
        this.inputPort.setToolTipText("listening port of the TuCSoN Node");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        this.chooseTC.add(this.inputPort, gridBagConstraints);

        this.buttonInspect.setFont(new java.awt.Font("Arial", 0, 11));
        this.buttonInspect.setText("Inspect!");
        this.buttonInspect
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            final java.awt.event.ActionEvent evt) {
                        InspectorGUI.this.buttonInspectActionPerformed();
                    }
                });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 30.0;
        this.chooseTC.add(this.buttonInspect, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.getContentPane().add(this.chooseTC, gridBagConstraints);

        this.imgPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(this.getClass().getResource(
                "/alice/tucson/images/logo.gif")));
        jLabel1.setPreferredSize(new java.awt.Dimension(90, 140));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        this.imgPanel.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        this.getContentPane().add(this.imgPanel, gridBagConstraints);

        this.stateBar.setBackground(Color.CYAN);
        this.stateBar.setEditable(false);
        this.stateBar.setText("Waiting user input...");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        this.getContentPane().add(this.stateBar, gridBagConstraints);

    }

    /**
     * Handles 'pending query' button.
     * 
     * @param evt
     *            'pending' button pushing event.
     */
    private void pendingBtnActionPerformed() {
        this.protocol
                .setPendingQueryObservType(InspectorProtocol.PROACTIVE_OBSERVATION);
        try {
            this.context.setProtocol(this.protocol);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.pendingQueryForm.setVisible(true);
    }

    /**
     * Handles 'edit specification space' button.
     * 
     * @param evt
     *            'specification' button pushing event.
     */
    private void specBtnActionPerformed() {
        this.specForm.setVisible(true);
    }

    /**
     * Handles 'inspect triggered reactions' button.
     * 
     * @param evt
     *            'reaction' button pushing event.
     */
    private void trigReactsBtnActionPerformed() {
        this.protocol
                .setReactionsObservType(InspectorProtocol.PROACTIVE_OBSERVATION);
        try {
            this.context.setProtocol(this.protocol);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.reactionForm.setVisible(true);
    }

    /**
     * Handles 'inspect tuples' button.
     * 
     * @param evt
     *            'tuples' button pushing event.
     */
    private void tuplesBtnActionPerformed() {
        this.protocol
                .setTsetObservType(InspectorProtocol.PROACTIVE_OBSERVATION);
        try {
            this.context.setProtocol(this.protocol);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        this.tupleForm.setVisible(true);
    }

}
