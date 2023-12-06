import json

import pandas as pd
from numpy import loadtxt


file = './psp_unused.csv'
unused = loadtxt(file, dtype=str, comments="#", delimiter=",", unpack=False)
# print(unused)

def crea_oggetto_json(df):
    result = []
    for _, riga in df.iterrows():
        cf = riga['CF']
        psp_code = riga['PSP_CODE']
        bic = riga['BIC']
        abi = riga['ABI']

        if psp_code in unused:
            continue

        if not any(x for x in result if x['CF'] == cf):
            result.append({'CF': cf, 'BIC': [], 'ABI': []})

        if bic is not None and bic.upper() != 'TBD' and not psp_code.startswith('ABI'):
            elem = [x for x in result if x['CF'] == cf][0]
            elem['BIC'].append(psp_code)

        if abi is not None and abi.upper() != 'TBD' and psp_code.startswith('ABI'):
            elem = [x for x in result if x['CF'] == cf][0]
            elem['ABI'].append(psp_code)

    return result


# Leggi il file CSV
nome_file_csv = './psp_duplicated.csv'
dati = pd.read_csv(nome_file_csv, dtype=str, keep_default_na=False)

# Crea l'oggetto JSON
oggetto_json = crea_oggetto_json(dati)

# Stampa l'oggetto JSON
# print(json.dumps(oggetto_json, indent=2))

# Se vuoi salvare l'oggetto JSON in un file
with open('output.json', 'w') as file_json:
    json.dump(oggetto_json, file_json, indent=2)
