Library IEEE;
use IEEE.std_logic_1164.all;

entity tb_bin2ssd is
begin
end entity;

architecture testbench of tb_bin2ssd is

  component bin2ssd is
    port (
      bin : in std_logic_vector(3 downto 0);
      abcdefg : out std_logic_vector(6 downto 0)
    );
  end component;

  component seg7model is
    port (
      c : in std_logic;
      abcdefg : in std_logic_vector(6 downto 0);
      disp1 : out std_logic_vector(3 downto 0);
      disp0 : out std_logic_vector(3 downto 0)
    );
  end component;

  signal tb_bin : std_logic_vector(3 downto 0);
  signal tb_disp0 : std_logic_vector(3 downto 0);
  signal tb_disp1 : std_logic_vector(3 downto 0);
  signal tb_abcdefg : std_logic_vector(6 downto 0);
  signal tb_c : std_logic := '0';

  begin
    
    UUT: entity work.bin2ssd(rtl)
      port map (
        bin => tb_bin,
        abcdefg => tb_abcdefg
      );

    model: entity work.seg7model(beh)
      port map (
        c => tb_c,
        abcdefg => tb_abcdefg,
        disp1 => tb_disp1,
        disp0 => tb_disp0
      );
    
    tb_bin <= "0000",
              "0001" after 100 ns,
              "0011" after 200 ns,
              "1110" after 300 ns;

    tb_c <= '0', '1' after 150 ns, '0' after 250 ns;
end testbench;
      
