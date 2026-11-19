import json, tempfile, unittest, subprocess, sys
from pathlib import Path
from data_io import read_records
class DataTest(unittest.TestCase):
 def test_formats_and_filter(self):
  with tempfile.TemporaryDirectory() as d:
   p=Path(d); rows=[{'business_id':'a','latitude':1,'longitude':2},{'business_id':'b','latitude':8,'longitude':9}]
   for name,content in [('array.json',json.dumps(rows)),('lines.json','\n'.join(map(json.dumps,rows)))]:
    source=p/name;source.write_text(content)
    self.assertEqual(list(read_records(source)),rows)
    out=p/(name+'.out')
    command=[sys.executable,str(Path(__file__).with_name('filter_data.py')),str(source),str(out),'--bounds','0','3','0','3']
    subprocess.run(command,check=True,capture_output=True)
    self.assertEqual(list(read_records(out)),rows[:1])
    self.assertNotEqual(subprocess.run(command,capture_output=True).returncode,0)
if __name__=='__main__':unittest.main()
