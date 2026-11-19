import json
from pathlib import Path
def read_records(path):
    with Path(path).open(encoding="utf-8-sig") as f:
        first=""
        while True:
            c=f.read(1)
            if not c: return
            if not c.isspace(): first=c; break
        f.seek(0)
        if first == "[":
            data=json.load(f)
            if not isinstance(data,list): raise ValueError("Expected an array")
            for record in data:
                if not isinstance(record,dict): raise ValueError("Expected objects")
                yield record
        else:
            for line in f:
                if line.strip():
                    record=json.loads(line)
                    if not isinstance(record,dict): raise ValueError("Expected objects")
                    yield record
