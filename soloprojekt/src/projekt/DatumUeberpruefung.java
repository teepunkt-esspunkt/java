package projekt;

import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class DatumUeberpruefung {
	public String dbDatum;
/*
 * Klasse zur Ueberpruefung des Datums 
 *	ob das Format dd.MM.yyyy vorliegt
 *  und ob das Datum in der Vergangenheit liegt
 */
	public DatumUeberpruefung(String datumString, JTextField datumFeld, JLabel fehlerMeldung) {
		fehlerMeldung.setVisible(false);

		// Format festlegen
		DateFormat eingabeDatumFormat = new SimpleDateFormat("dd.MM.yyyy");
		eingabeDatumFormat.setLenient(false);

		// Heutiges Datum festlegen
		Date heute = new Date();
		try {
			DateFormat dbDatumFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date eingabeDatum = eingabeDatumFormat.parse(datumString);
			dbDatum = dbDatumFormat.format(eingabeDatum);
			fehlerMeldung.setVisible(false);
			// Prüfen ob Datum in der Vergangenheit liegt und gültig ist, Fehler wenn nicht
			if (!eingabeDatum.after(heute) && istDatumGueltig(datumString)) {
				fehlerMeldung.setVisible(false);
			} else {
				fehlerMeldung.setVisible(true);
				datumFeld.setText("");
			}

		} catch (ParseException pe) {
			if (datumString.length() >= 1) {
				fehlerMeldung.setVisible(true);
			}
			datumFeld.setText("");
		}
	}

	public boolean istDatumGueltig(String datumString) {
		
//		//Regex um eingaben wie 1.1.1909 oder 21.02.2091f zu verhindern. Zeitraum: 1900 -2099
//		Pattern datumFormat = Pattern.compile("^(0[1-9]|[12][0-9]|3[01]).(0[1-9]|1[0-2]).(19|20)\\d{2}$");
		//Regex um eingaben wie 1.1.1909 oder 21.02.2091f zu verhindern. Zeitraum: 2000 -2099
		Pattern datumFormat = Pattern.compile("^(0[1-9]|[12][0-9]|3[01]).(0[1-9]|1[0-2]).20\\d{2}$");
		if (datumFormat.matcher(datumString).matches()) {
			return true;
		} else {
			return false;

		}
	}
//	getter
	public String getDbDatum() {
		return dbDatum;
	}

}
