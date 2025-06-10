

def arfcn_to_frequency(arfcn : int, network_type : str) -> float:
    
    if arfcn is None or network_type is None:
        return None
    
    if network_type in ('GSM', 'GPRS', 'EDGE'):
        # GSM bands
        if 0 <= arfcn <= 124:
            return 890.0 + 0.2 * arfcn  # GSM 900
        elif 955 <= arfcn <= 1023:
            return 890.0 + 0.2 * (arfcn - 1024)  # GSM 900 Extended
        elif 512 <= arfcn <= 885:
            return 1710.2 + 0.2 * (arfcn - 512)  # DCS 1800
        elif 128 <= arfcn <= 251:
            return 824.2 + 0.2 * (arfcn - 128)  # GSM 850
        elif 512 <= arfcn <= 810:
            return 1850.2 + 0.2 * (arfcn - 512)  # PCS 1900
        else:
            return None
    
    elif network_type in ('UMTS', 'HSPA', 'HSPA+'):
        # UMTS bands
        if 0 <= arfcn <= 124:
            return 2110.0 - 0.2 * (10562 - arfcn)  # Band I (2100)
        elif 512 <= arfcn <= 885:
            return 1930.0 - 0.2 * (9662 - arfcn)  # Band II (1900)
        elif 256 <= arfcn <= 511:
            return 1805.0 - 0.2 * (9262 - arfcn)  # Band III (1800)
        elif 1537 <= arfcn <= 1738:
            return 2110.0 - 0.2 * (1537 - arfcn)  # Band IV (1700/2100)
        elif 4357 <= arfcn <= 4458:
            return 869.0 - 0.1 * (4357 - arfcn)  # Band V (850)
        elif 4387 <= arfcn <= 4413:
            return 2620.0 - 0.2 * (4387 - arfcn)  # Band VII (2600)
        else:
            return None
    
    elif network_type in ('LTE', 'LTE-Adv'):
        # LTE bands (بر اساس جدول 3GPP TS 36.101)
        if 0 <= arfcn <= 599:
            return 2110.0 + 0.1 * (arfcn - 0)  # Band 1 (2100)
        elif 600 <= arfcn <= 1199:
            return 1930.0 + 0.1 * (arfcn - 600)  # Band 2 (1900)
        elif 1200 <= arfcn <= 1949:
            return 1805.0 + 0.1 * (arfcn - 1200)  # Band 3 (1800)
        elif 1950 <= arfcn <= 2399:
            return 2110.0 + 0.1 * (arfcn - 1950)  # Band 4 (1700/2100)
        elif 2400 <= arfcn <= 2649:
            return 869.0 + 0.1 * (arfcn - 2400)  # Band 5 (850)
        elif 2650 <= arfcn <= 2749:
            return 875.0 + 0.1 * (arfcn - 2650)  # Band 6 (800)
        elif 2750 <= arfcn <= 3449:
            return 2620.0 + 0.1 * (arfcn - 2750)  # Band 7 (2600)
        elif 3450 <= arfcn <= 3799:
            return 925.0 + 0.1 * (arfcn - 3450)  # Band 8 (900)
        elif 3800 <= arfcn <= 4149:
            return 1844.9 + 0.1 * (arfcn - 3800)  # Band 9 (1800)
        elif 4150 <= arfcn <= 4749:
            return 2110.0 + 0.1 * (arfcn - 4150)  # Band 10 (1700/2100)
        elif 5010 <= arfcn <= 5179:
            return 1475.9 + 0.1 * (arfcn - 5010)  # Band 11 (1500)
        elif 5180 <= arfcn <= 5279:
            return 729.9 + 0.1 * (arfcn - 5180)  # Band 12 (700)
        elif 5280 <= arfcn <= 5379:
            return 746.0 + 0.1 * (arfcn - 5280)  # Band 13 (700)
        elif 5730 <= arfcn <= 5849:
            return 758.0 + 0.1 * (arfcn - 5730)  # Band 14 (700)
        elif 5760 <= arfcn <= 5999:
            return 869.0 + 0.1 * (arfcn - 5760)  # Band 17 (700)
        elif 6000 <= arfcn <= 6149:
            return 791.0 + 0.1 * (arfcn - 6000)  # Band 18 (800)
        else:
            return None
    
    elif network_type == '5G':
        # NR bands (5G)
        if 0 <= arfcn <= 2016667:
            # FR1 (Sub-6 GHz)
            if 422000 <= arfcn <= 434000:
                return 617.0 + 0.015 * (arfcn - 422000)  # n71 (600 MHz)
            elif 386000 <= arfcn <= 398000:
                return 703.0 + 0.015 * (arfcn - 386000)  # n12 (700 MHz)
            elif 399000 <= arfcn <= 404000:
                return 728.0 + 0.015 * (arfcn - 399000)  # n28 (700 MHz)
            elif 120000 <= arfcn <= 130000:
                return 1930.0 + 0.015 * (arfcn - 120000)  # n1 (2100 MHz)
            elif 185000 <= arfcn <= 191000:
                return 859.0 + 0.015 * (arfcn - 185000)  # n5 (850 MHz)
            else:
                return None
        else:
            # FR2 (mmWave)
            return None
    
    else:
        return None