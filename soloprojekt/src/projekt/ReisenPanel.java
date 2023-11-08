package projekt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.AttributedString;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ReisenPanel extends JPanel implements ActionListener, FocusListener {

	// fuer die Verbindung zur Datenbank
	private DbVerbindungen dbVerbindung;
	private Connection conn;
	private Statement stmt;
	private ResultSet result;

	// Buttons
	// Button zum Resetten der Eingabefelder
	private JButton reset = new JButton("Reset");
	// Button zum Speichern in die Datenbank
	private JButton speichern = new JButton("Speichern");

	// Labels
	private JLabel datum = new JLabel("Datum");
	private JLabel name = new JLabel("Name");
	private JLabel vorname = new JLabel("Vorname");
	private JLabel strasse = new JLabel("Straße");
	private JLabel ort = new JLabel("Ort");
	private JLabel plz = new JLabel("PLZ");
	// Textfelder
	private JTextField datumFeld = new JTextField();
	private JTextField nameFeld = new JTextField();
	private JTextField vornameFeld = new JTextField();
	private JTextField strasseFeld = new JTextField();
	private JTextField plzFeld = new JTextField();
	private JTextField ortFeld = new JTextField();
	// Überschriften für die "Tabelle"
	private JLabel kostenartTitel = new JLabel("KOSTENART");
	private JLabel anzahlTitel = new JLabel("ANZAHL");
	private JLabel einzelverguetungTitel = new JLabel("EINZELVERGÜTUNG");
	private JLabel gesamtverguetungTitel = new JLabel("GESAMTVERGÜTUNG");
	// Zaehler zum zaehlen der Kostenarten
	private int zaehler = 0;

	// zum speichern der k_ids, kostenartbezeichnungen und einzelverguetungen
	private int[] k_idArray;
	private String[] kosteneintrag;
	private double[] einzelverguetungseintrag;

	// Vier Arrays für die Tabelle
	private JLabel[] kostenarten;
	private JLabel[] einzelverguetungen;
	private double[] gesamtverguetungen;
	private JTextField[] anzahl;

	// Fehlermeldungen
	private JLabel fehlerAnzahl;
	private JLabel fehlerLeer;
	private JLabel plzFehlermeldung;
	private JLabel datumFehlermeldung;

	// für die Datumsübergabe
	private String dbDatum;

	// erfolgsmeldung bei erfolgreichem Speichern
	private JLabel erfolgMeldung;

	// variable zur Leerzellen Überprüfung der Anzahl bei Kostenarten
	boolean anzahlAlleDa = false;

	public ReisenPanel() {
		// Layoutmanager bestimmen
		setLayout(null);

		// Erfolgmeldung bei Speichern in die Datenbank
		erfolgMeldung = new JLabel("Speicherung Erfolgreich");
		erfolgMeldung.setForeground(Color.BLUE);
		erfolgMeldung.setBounds(150, 120, 200, 20);
		erfolgMeldung.setVisible(false);
		add(erfolgMeldung);

		// FehlerMeldungen
		// Fehlermeldung für die Postleitzahl
		plzFehlermeldung = new JLabel("Bitte eine gültige Postleitzahl angeben");
		plzFehlermeldung.setForeground(Color.RED);
		plzFehlermeldung.setBounds(200, 280, 300, 20);
		plzFehlermeldung.setVisible(false);
		add(plzFehlermeldung);
		plzFeld.addFocusListener(this);
		plzFeld.addActionListener(this);

		// Fehlermeldung für das Datum
		datumFehlermeldung = new JLabel("Gültiges Datum eingeben(tt.mm.jjjj)");
		datumFehlermeldung.setForeground(Color.RED);
		datumFehlermeldung.setBounds(560, 120, 200, 20);
		datumFehlermeldung.setVisible(false);
		add(datumFehlermeldung);
		datumFeld.addFocusListener(this);
		datumFeld.addActionListener(this);

		// Anordnung der Labels und Textfelder, der Vorlage entsprechend
		// datum mit Überprüfung, Jahresbereich 1900 - 2099
		datum.setBounds(500, 100, 100, 20);
		add(datum);
		datumFeld.setBounds(560, 100, 100, 20);
		add(datumFeld);

		name.setBounds(50, 150, 100, 20);
		add(name);
		nameFeld.setBounds(100, 150, 100, 20);
		add(nameFeld);

		vorname.setBounds(500, 150, 100, 20);
		add(vorname);
		vornameFeld.setBounds(560, 150, 100, 20);
		add(vornameFeld);

		strasse.setBounds(50, 200, 100, 20);
		add(strasse);
		strasseFeld.setBounds(100, 200, 300, 20);
		add(strasseFeld);

		// postleitzahlen feld mit Überprüfung nach deutschen Postleitzahlen
		plz.setBounds(50, 250, 100, 20);
		add(plz);
		plzFeld.setBounds(100, 250, 100, 20);
		add(plzFeld);

		ort.setBounds(500, 250, 100, 20);
		add(ort);
		ortFeld.setBounds(560, 250, 100, 20);
		add(ortFeld);

		// Die Überschriften der "Tabelle"
		kostenartTitel.setBounds(50, 390, 150, 20);
		add(kostenartTitel);

		anzahlTitel.setBounds(200, 390, 150, 20);
		add(anzahlTitel);

		einzelverguetungTitel.setBounds(350, 390, 150, 20);
		add(einzelverguetungTitel);

		gesamtverguetungTitel.setBounds(580, 390, 150, 20);
		add(gesamtverguetungTitel);

		// Verbindung zur Datenbank zum Auslesen von Kostenart und Einzelvergütung
		dbVerbindung = new DbVerbindungen();
		dbVerbindung.dbVerbindungStarten();
		conn = dbVerbindung.getConn();
		// Kostenarten und Einzelverguetungen aus der Datenbank auslesen
		try {
			// SQL Befehl
			String sql = "SELECT k_id, kostenart, einzelverguetung FROM kostenarten";

			// statement vorbereiten und den Zeiger zuruecksetzbar machen
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// SQL Befehl an die DB schicken
			result = stmt.executeQuery(sql);

			// Zählen der Einträge, wieviele Zeilen soll die "Tabelle" haben
			while (result.next()) {
				zaehler++;
			}

			// Arrays mit der dem Resultat des Zaehlers anlegen
			kostenarten = new JLabel[zaehler];
			einzelverguetungen = new JLabel[zaehler];
			gesamtverguetungen = new double[zaehler];
			k_idArray = new int[zaehler];
			kosteneintrag = new String[zaehler];
			einzelverguetungseintrag = new double[zaehler];
			// zeiger an den anfang setzen
			result.beforeFirst();

			// Ausgabe der Spalten der Tabelle, auf den Einträgen in der Datenbank basierend
			for (int i = 0; result.next(); i++) {

				kosteneintrag[i] = result.getString("kostenart");
				kostenarten[i] = new JLabel(kosteneintrag[i].toString());
				kostenarten[i].setBounds(50, 420 + 30 * i, 100, 20);
				add(kostenarten[i]);
				einzelverguetungseintrag[i] = result.getDouble("einzelverguetung");
				einzelverguetungen[i] = new JLabel(String.valueOf(einzelverguetungseintrag[i]));
				einzelverguetungen[i].setHorizontalAlignment(JLabel.CENTER);
				einzelverguetungen[i].setBounds(350, 420 + 30 * i, 100, 20);
				add(einzelverguetungen[i]);
//				 k_ids bei der Gelegenheit auch gleich speichern
				k_idArray[i] = result.getInt("k_id");

			}
			// Verbindung schließen
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbVerbindung.dbVerbindungTrennen();
		}
		// Fehlermeldungen Textfeld Anzahl benötigt Resultat des Zählers für Platzierung
		fehlerAnzahl = new JLabel("Fehler: Bitte geben Sie eine gültige Anzahl ein");
		fehlerAnzahl.setForeground(Color.RED);
		fehlerAnzahl.setBounds(200, 420 + 30 * (zaehler + 4), 300, 20);
		fehlerAnzahl.setVisible(false);
		add(fehlerAnzahl);

		// FehlerMeldung Leere Felder benötigt Resultat des Zählers für Platzierung
		fehlerLeer = new JLabel("Persönliche Daten min. 2 Zeichen. max. 200");
		fehlerLeer.setForeground(Color.RED);
		fehlerLeer.setBounds(350, 420 + 30 * (zaehler + 3), 300, 20);
		fehlerLeer.setVisible(false);
		add(fehlerLeer);

		// button platzierung benötigt Resultat des Zählers
		reset.setBounds(200, 420 + 30 * (zaehler + 1), 100, 20);
		speichern.setBounds(200, 420 + 30 * (zaehler + 2), 100, 20);
		add(reset);
		add(speichern);
		reset.addActionListener(this);
		speichern.addActionListener(this);

		// Ausgabe zweiten Spalte - AnzahlTextfeld - Userinput - Teil der Berechnung
		anzahl = new JTextField[zaehler];
		for (int i = 0; i < zaehler; i++) {
			anzahl[i] = new JTextField();
			anzahl[i].setBounds(200, 420 + 30 * i, 100, 20);
			add(anzahl[i]);
			anzahl[i].addActionListener(this);
			anzahl[i].addFocusListener(this);
		}

	}

	// zum speichern in die datenbank
	public void inDbSpeichern(String dbDatum, String name, String vorname, String strasse, String plz, String ort,
			int[] anzahlWerte, int[] k_idWerte) {

		// wurden alle Personen Daten ausgefüllt?
		if (datumFeld.getText().length() >= 2 && vornameFeld.getText().length() >= 2
				&& vornameFeld.getText().length() <= 200 && nameFeld.getText().length() >= 2
				&& nameFeld.getText().length() <= 200 && strasseFeld.getText().length() >= 2
				&& strasseFeld.getText().length() <= 200 && plzFeld.getText().length() >= 2
				&& ortFeld.getText().length() >= 2 && ortFeld.getText().length() <= 200) {
			fehlerLeer.setVisible(false);

			// wurden alle Kostenarten ausgefüllt?
			for (int i = 0; i < zaehler; i++) {
				if (anzahl[i].getText().isEmpty()) {
					fehlerAnzahl.setVisible(true);
					anzahlAlleDa = false;
					break;
				} else {
					anzahlAlleDa = true;
				}
			}
			// wenn alles ausgefuellt wurde
			if (anzahlAlleDa) {
				// neue Verbindung aufbauen
				dbVerbindung.dbVerbindungNeustarten();
				conn = dbVerbindung.getConn();
				try {
					// sql Statement vorbereiten
					String sql = "INSERT INTO personen (datum, name, vorname, strasse, plz, ort) VALUES (?, ?, ?, ?, ?, ?)";
					PreparedStatement stmtPersonen = conn.prepareStatement(sql);
					// sql statement Werte einfuegen
					stmtPersonen.setString(1, dbDatum);
					stmtPersonen.setString(2, name);
					stmtPersonen.setString(3, vorname);
					stmtPersonen.setString(4, strasse);
					stmtPersonen.setString(5, plz);
					stmtPersonen.setString(6, ort);
					stmtPersonen.executeUpdate();
					// ID Bestimmung
					int personenId = -1;
					String getPersonenIdDb = "SELECT LAST_INSERT_ID() AS id";
					PreparedStatement getPid = conn.prepareStatement(getPersonenIdDb);
					ResultSet personenIdResultat = getPid.executeQuery();
					if (personenIdResultat.next()) {
						personenId = personenIdResultat.getInt("id");
					}
					// weiteres Statement vorbereiten
					String abrechnungSql = "INSERT INTO abrechnungen (p_id, k_id, anzahl) VALUES (?, ?, ?)";
					PreparedStatement abrechnungStmt = conn.prepareStatement(abrechnungSql);
					// Werte einfuegen
					for (int j = 0; j < anzahlWerte.length; j++) {
						abrechnungStmt.setInt(1, personenId);
						abrechnungStmt.setInt(2, k_idWerte[j]);
						abrechnungStmt.setInt(3, anzahlWerte[j]);
						abrechnungStmt.executeUpdate();
					}
					// abschicken
					conn.commit();
					// Statements schliessen
					abrechnungStmt.close();
					stmtPersonen.close();
					// Textfelder leeren
					datumFeld.setText("");
					nameFeld.setText("");
					vornameFeld.setText("");
					strasseFeld.setText("");
					plzFeld.setText("");
					ortFeld.setText("");
					// Erfolgmeldung
					erfolgMeldung.setVisible(true);
					// Anzahl Textfelder leeren
					for (int ii = 0; ii < zaehler; ii++) {
						anzahl[ii].setText("");
						gesamtverguetungen[ii] = 0;
					}
					fehlerLeer.setVisible(false);
					repaint();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					// Datenbank Verbindung schliessen
					dbVerbindung.dbVerbindungTrennen();
				}
			} else {
				fehlerAnzahl.setVisible(true);
			}
		} else {
			fehlerLeer.setVisible(true);
		}

	}

	// paintComponent, für die Überschrift und die gesamtvergütung (berechnung)
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Überschrift ausgeben und formatieren
		// überschriften Text
		String ueberschrift = "Reisekostenabrechnung";
		// AttributeString für mehrere Attribute
		AttributedString fettUnterstrichen = new AttributedString(ueberschrift);
		// Überschrift fetten
		fettUnterstrichen.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		// Überschrift unterstreichen
		fettUnterstrichen.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, ueberschrift.length());
		// Überschriften größe
		fettUnterstrichen.addAttribute(TextAttribute.SIZE, 28);
		// wird benötigt zum zentrieren
		Graphics2D mittigCast = (Graphics2D) g;
		FontRenderContext titelMittig = mittigCast.getFontRenderContext();
		TextLayout titelLayout = new TextLayout(fettUnterstrichen.getIterator(), titelMittig);
		double titelStart = (getWidth() - titelLayout.getAdvance()) / 2;
		// die Überschrift
		g.drawString(fettUnterstrichen.getIterator(), (int) titelStart, 50);

		// die Gesamtkosten pro Kostenart, anzahl mal einzelVerguetung
		for (int i = 0; i < zaehler; i++) {
			String gesamtKosten = String.format("%.2f", gesamtverguetungen[i]);
			g.drawString(gesamtKosten, 660, 434 + 30 * i);
		}
	}

	// Prüfung der PLZ
	public boolean plzPruefung(String plz) {
		try {
			int plzWert = Integer.parseInt(plz);
			return plzWert >= 1067 && plzWert <= 99998;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// Berechnung für gesamtverguetung
	public double gesamtverguetungRechnung(double einzelverguetung, int anzahl) {
		return einzelverguetung * anzahl;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Reset button
		if (e.getSource() == reset) {
			// Alle Textfelder leeren
			datumFeld.setText("");
			nameFeld.setText("");
			vornameFeld.setText("");
			strasseFeld.setText("");
			plzFeld.setText("");
			ortFeld.setText("");
			// Anzahl und Gesamtverguetung Felder leeren
			for (int i = 0; i < zaehler; i++) {
				anzahl[i].setText("");
				gesamtverguetungen[i] = 0;
			}
			// Fehlermeldung entfernen
			fehlerAnzahl.setVisible(false);
			plzFehlermeldung.setVisible(false);
			datumFehlermeldung.setVisible(false);
			fehlerLeer.setVisible(false);
			erfolgMeldung.setVisible(false);
			repaint();
			// Speichern button
		} else if (e.getSource() == speichern) {
			// DB Verbindung neu starten
			dbVerbindung.dbVerbindungNeustarten();
			conn = dbVerbindung.getConn();

			int[] anzahlWerte = new int[zaehler];
			// Anzahl pro Kostenart
			for (int i = 0; i < zaehler; i++) {
				try {
					anzahlWerte[i] = Integer.parseInt(anzahl[i].getText());
				} catch (NumberFormatException ex) {
				}
			}
			// Speichern
			inDbSpeichern(dbDatum, nameFeld.getText(), vornameFeld.getText(), strasseFeld.getText(), plzFeld.getText(),
					ortFeld.getText(), anzahlWerte, k_idArray);
			// Verbindung trennen
			dbVerbindung.dbVerbindungTrennen();
			// PLZ Feld (mit Prüfung)
		} else if (e.getSource() == plzFeld) {
			JTextField source = (JTextField) plzFeld;
			// Eingabe auslesen
			String plzEingabe = source.getText();
			// Postleitzahl pruefen
			if (!plzPruefung(plzEingabe)) {
				plzFehlermeldung.setVisible(true);
				source.setText("");
			} else {
				// Fehlermeldung anzeigen wenn ungueltig
				plzFehlermeldung.setVisible(false);
			}
			// Datumfeld mit Prüfung
		} else if (e.getSource() == datumFeld) {
			// Datum auslesen
			String datumString = datumFeld.getText().trim();
			// Überprüfung
			DatumUeberpruefung dp = new DatumUeberpruefung(datumString, datumFeld, datumFehlermeldung);
			dbDatum = dp.getDbDatum();
			// Anzahl Felder
		} else {
			for (int i = 0; i < zaehler; i++) {
				if (e.getSource() == anzahl[i]) {
					int index = i;
					JTextField source = (JTextField) e.getSource();
					String eingabe = source.getText();
					// Wenn etwas drin steht soll gerechnet werden
					if (!eingabe.isEmpty()) {
						try {
							int anzahlWert = Integer.parseInt(eingabe);
							double einzelverguetung = Double.parseDouble(einzelverguetungen[index].getText());
							// Rechnung
							gesamtverguetungen[index] = gesamtverguetungRechnung(einzelverguetung, anzahlWert);
							repaint();
						} catch (NumberFormatException nfe) {
							fehlerAnzahl.setVisible(true);
							// Bei Fehler leeren
							for (int ii = 0; ii < zaehler; ii++) {
								anzahl[ii].setText("");
							}
						}
					} else {
						fehlerAnzahl.setVisible(true);
					}

				}
			}
		}

	}

	@Override
	public void focusLost(FocusEvent e) {
		// Datums Fehlermeldung bei wechseln des Textfeldes
		if (e.getSource() == datumFeld) {
			String datumString = datumFeld.getText().trim();
			DatumUeberpruefung dp2 = new DatumUeberpruefung(datumString, datumFeld, datumFehlermeldung);
			dbDatum = dp2.getDbDatum();
		} else if (e.getSource() == plzFeld) {
			JTextField source = (JTextField) plzFeld;
			// Eingabe auslesen
			String plzEingabe = source.getText();
			// Postleitzahl pruefen
			if (!plzPruefung(plzEingabe)) {
				plzFehlermeldung.setVisible(true);
				source.setText("");
			} else {
				// Fehlermeldung anzeigen wenn ungueltig
				plzFehlermeldung.setVisible(false);
			}
		} else {
			// anzahl
			for (int i = 0; i < zaehler; i++) {
				if (e.getSource() == anzahl[i]) {
					int index = i;
					JTextField source = (JTextField) e.getSource();
					String eingabe = source.getText();
					// Wenn etwas drin steht soll gerechnet werden
					if (!eingabe.isEmpty()) {
						try {
							int anzahlWert = Integer.parseInt(eingabe);
							double einzelverguetung = Double.parseDouble(einzelverguetungen[index].getText());
							// Rechnung
							gesamtverguetungen[index] = gesamtverguetungRechnung(einzelverguetung, anzahlWert);
							repaint();
						} catch (NumberFormatException nfe) {
							fehlerAnzahl.setVisible(true);
							// Bei Fehler leeren
							for (int ii = 0; ii < zaehler; ii++) {
								anzahl[ii].setText("");
							}
						}
					} else {
						fehlerAnzahl.setVisible(true);
					}

				}
			}
		}
	}

	// wegen des implements
	@Override
	public void focusGained(FocusEvent e) {

	}
}
