/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tucson.introspection.tools;

@SuppressWarnings("serial")
public class ReactionViewer extends javax.swing.JFrame
{
	private Inspector mainForm;

	/** Creates new form TupleForm */
	public ReactionViewer(Inspector mainForm_)
	{
		initComponents();
		pack();
		mainForm = mainForm_;
		setTitle("Triggered Reaction Set of " + mainForm.tid.getName() + "@" + mainForm.tid.getNode() + ":" + mainForm.tid.getPort());
		setSize(520, 460);
		inputFileLog.setText(mainForm.agent.logReactionFilename);
	}

	public void appendText(String st)
	{
		outputArea.append(st);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the FormEditor.
	 */
	private void initComponents()
	{// GEN-BEGIN:initComponents
		java.awt.GridBagConstraints gridBagConstraints;

		jScrollPane1 = new javax.swing.JScrollPane();
		outputArea = new javax.swing.JTextArea();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel5 = new javax.swing.JPanel();
		jPanel7 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		inputFileLog = new javax.swing.JTextField();
		buttonBrowse = new javax.swing.JButton();
		checkLogEnable = new javax.swing.JCheckBox();

		getContentPane().setLayout(new java.awt.GridBagLayout());

		setTitle("reactions executed");
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				exitForm(evt);
			}
		});

		outputArea.setFont(new java.awt.Font("Courier New", 0, 12));
		jScrollPane1.setViewportView(outputArea);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 100.0;
		gridBagConstraints.weighty = 100.0;
		getContentPane().add(jScrollPane1, gridBagConstraints);

		jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11));
		jPanel5.setLayout(new java.awt.GridBagLayout());

		jPanel7.setLayout(new java.awt.GridBagLayout());

		jPanel7.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "store"));
		jPanel7.setFont(new java.awt.Font("Arial", 0, 11));
		jLabel1.setFont(new java.awt.Font("Arial", 0, 11));
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel1.setText("log file   ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 10.0;
		jPanel7.add(jLabel1, gridBagConstraints);

		inputFileLog.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				inputFileLogActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 100.0;
		jPanel7.add(inputFileLog, gridBagConstraints);

		buttonBrowse.setFont(new java.awt.Font("Arial", 0, 11));
		buttonBrowse.setText("browse");
		buttonBrowse.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				buttonBrowseActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 5;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 5.0;
		jPanel7.add(buttonBrowse, gridBagConstraints);

		checkLogEnable.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				checkLogEnableActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		jPanel7.add(checkLogEnable, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 90.0;
		gridBagConstraints.weighty = 30.0;
		jPanel5.add(jPanel7, gridBagConstraints);

		jTabbedPane1.addTab("Log", null, jPanel5, "");

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 100.0;
		gridBagConstraints.weighty = 10.0;
		getContentPane().add(jTabbedPane1, gridBagConstraints);

	}// GEN-END:initComponents

	private void buttonBrowseActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_buttonBrowseActionPerformed
		javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION)
		{
			java.io.File file = chooser.getSelectedFile();
			if (file != null)
			{
				String name = file.getAbsolutePath();
				inputFileLog.setText(name);
				mainForm.agent.changeLogReactionFile(name);
			}
		}
	}// GEN-LAST:event_buttonBrowseActionPerformed

	private void inputFileLogActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_inputFileLogActionPerformed
		mainForm.agent.changeLogReactionFile(inputFileLog.getText());
	}// GEN-LAST:event_inputFileLogActionPerformed

	private void checkLogEnableActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_checkLogEnableActionPerformed
		if (checkLogEnable.isSelected())
		{
			mainForm.agent.loggingReactions = true;
		}
		else
		{
			mainForm.agent.loggingReactions = false;
		}
	}// GEN-LAST:event_checkLogEnableActionPerformed

	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt)
	{// GEN-FIRST:event_exitForm
		mainForm.onReactionViewerExit();
	}// GEN-LAST:event_exitForm

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JCheckBox checkLogEnable;
	private javax.swing.JTextField inputFileLog;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea outputArea;
	private javax.swing.JButton buttonBrowse;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JLabel jLabel1;
	// End of variables declaration//GEN-END:variables

}
