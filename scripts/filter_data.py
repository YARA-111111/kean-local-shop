import argparse,json
from pathlib import Path
from data_io import read_records
def main():
 p=argparse.ArgumentParser(description="Filter JSON arrays or JSONL; output JSONL")
 p.add_argument("input");p.add_argument("output")
 p.add_argument("--bounds",nargs=4,type=float,metavar=("LAT_MIN","LAT_MAX","LON_MIN","LON_MAX"))
 p.add_argument("--business-ids",help="Text file: one business id per line")
 a=p.parse_args()
 if Path(a.input).resolve()==Path(a.output).resolve():p.error("Output must differ from input")
 if not a.bounds and not a.business_ids:p.error("Provide --bounds or --business-ids")
 ids=set(Path(a.business_ids).read_text().splitlines()) if a.business_ids else None
 if a.bounds and (a.bounds[0]>a.bounds[1] or a.bounds[2]>a.bounds[3]):p.error("Invalid bounds")
 with open(a.output,"x",encoding="utf-8") as out:
  for row in read_records(a.input):
   if ids is not None and row.get("business_id") not in ids:continue
   if a.bounds:
    lat,lon=row.get("latitude"),row.get("longitude");b=a.bounds
    if lat is None or lon is None or not(b[0]<=lat<=b[1] and b[2]<=lon<=b[3]):continue
   out.write(json.dumps(row,ensure_ascii=False)+"\n")
if __name__=="__main__":main()
