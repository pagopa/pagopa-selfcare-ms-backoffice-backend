import sys
import pandas as pd
from PyQt5.QtWidgets import QApplication, QWidget, QPushButton, QFileDialog, QLabel, QVBoxLayout

class FileSelectionApp(QWidget):

    file_path =  None
    
 
    def __init__(self):
        super().__init__()

        self.init_ui()

    def init_ui(self):
        self.setWindowTitle("Seleziona un file")
        self.setGeometry(150, 150, 600, 300)

        self.layout = QVBoxLayout()

        self.label = QLabel("Nessun file selezionato")
        self.layout.addWidget(self.label)
        print('.......Seleziona un file')
        self.select_button = QPushButton("Seleziona un file...")
        self.select_button.clicked.connect(self.open_file_dialog)
        
        self.layout.addWidget(self.select_button)

        self.setLayout(self.layout)
        
       
 
    def open_file_dialog(self):
        options = QFileDialog.Options()
        file_path, _ = QFileDialog.getOpenFileName(self, "Seleziona un file", "", "Tutti i file (*)", options=options)
        

        if file_path:
            self.label.setText("File selezionato: " + file_path)
            self.file_path = file_path
            
            self.select_button = QPushButton("Genera...")
            self.select_button.clicked.connect(lambda: self.convert_csv(self.file_path))
            self.label.setText("File generato: " + file_path + '_generato.csv')
            self.layout.addWidget(self.select_button)
            self.setLayout(self.layout)
        

           
            
             
        
    def convert_csv(self,file_path):
        print('file_path: ',file_path)
        # Leggi il file CSV con il punto e virgola (;) come separatore
        df = pd.read_csv(file_path, delimiter=';')
        print('##################DATAFRAME INIZIALE################################')
        print(df)
        df = df.fillna('2000')
        df.columns = ['taxCode','referent','name','email','telephone','modifiedAt','modifiedBy']
        print('dataframe....')
        print(df)
 
        df.insert(0,'_id',df['taxCode'])
        column_position_ma = df.columns.get_loc('modifiedAt')
        column_position_mb = df.columns.get_loc('modifiedBy')
        # Cicla attraverso le righe del DataFrame e formatta le date
        for index, row in df.iterrows():
            date_str = row['modifiedAt']
           
            # Verifica se la cella contiene un valore
            if date_str:
                date_components = date_str.split('/') if '/' in date_str else date_str
                if len(date_components) == 3:
                    day, month, year = date_components
                    formatted_date = f"{year}-{month.zfill(2)}-{day.zfill(2)}T00:00:00.000000000Z"
                elif len(date_components) == 2:
                    year, month = date_components
                    formatted_date = f"{year}-{month.zfill(2)}-01T00:00:00.000000000Z"
                else:
                    year = '2000'
                    formatted_date = f"{year}-01-01T00:00:00.000000000Z"
               
                # Sovrascrivi il valore originale con il nuovo valore formattato
                df.at[index, 'modifiedAt'] = formatted_date
        df.insert(column_position_ma,'createdAt',df['modifiedAt'])
        df.insert(column_position_mb,'createdBy',df['modifiedBy'])
        print('##################DATAFRAME AGGIORNATO################################')
        print(df)
        # Salva il DataFrame modificato in un nuovo file CSV
        df.to_csv(file_path + '_generato.csv', sep=';', index=False)



if __name__ == '__main__':
    app = QApplication(sys.argv)
    window = FileSelectionApp()
    window.show()
    sys.exit(app.exec_())
