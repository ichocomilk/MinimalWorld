<h1>MinWorld</h1>
<p>
    Minimal world format (.minworld)
    Only store blocks id  
</p>
<br>
<h1>How is made?</h1>
<ul>
    <li>The file format contains a header</li>
    <li>(Header = Version of file)</li>
    <li>Amount of chunks to read</li>
    <li>Chunks data</li>
</ul>
<img src="github/mwformat.png" >


<h2>How is chunk data stored?</h2>
<ol>
    <li>
        <h3>Header:</h3>
        <p>Compressed X and Z.</p>
        <p>Example: X: 4 and Z: 2</p>
        <p>Compressed: X << 32 | Z - First 32 bits = X and other = Z</p> 
    </li>
    <li>
        <h3>Amount chunks sections</h3>
        <p>Byte that represent amount of valid section in chunk</p>
        <p>(Valid section = no empty section, with blocks)</p>
    </li>
    <li>
        <h3>Chunk sections:</h3>
        <p>ID: a byte in range of (0-16)</p>
        <p>A array of int16, contains all block data</p>
    </li>
</ol>
<img src="github/chunkformat.png">
