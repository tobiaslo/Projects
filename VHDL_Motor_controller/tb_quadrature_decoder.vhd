Library IEEE;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity tb_quadrature_decoder is
end entity;

architecture testbench of tb_quadrature_decoder is

  component quadrature_decoder is
    port (
      clk : in std_logic;
      reset : in std_logic;
      sa : in std_logic;
      sb : in std_logic;
      pos_inc : out std_logic;
      pos_dec : out std_logic
    );
  end component;

  signal tb_clk : std_logic := '1';
  signal tb_reset : std_logic := '0';
  signal tb_sa : std_logic := '0';
  signal tb_sb : std_logic := '0';
  signal tb_pos_inc : std_logic;
  signal tb_pos_dec : std_logic;

begin

  UUT : quadrature_decoder
    port map (
      clk => tb_clk,
      reset => tb_reset,
      sa => tb_sa,
      sb => tb_sb,
      pos_inc => tb_pos_inc,
      pos_dec => tb_pos_dec
    );

  tb_clk <= not tb_clk after 5 ns;

  process is
  begin
    tb_reset <= '1';
    wait for 10 ns;
    tb_reset <= '0';
    wait for 10 ns;
    
    --Pos inc
    for i in 1 to 2 loop
      tb_sa <= '0';
      tb_sb <= '0';
      wait for 20 ns;
      tb_sa <= '0';
      tb_sb <= '1';
      wait for 20 ns;
      tb_sa <= '1';
      tb_sb <= '1';
      wait for 20 ns;
      tb_sa <= '1';
      tb_sb <= '0';
      wait for 20 ns;
      tb_sa <= '0';
      tb_sb <= '0';          
    end loop;

    --Pos dec
    for i in 1 to 2 loop
      tb_sa <= '0';
      tb_sb <= '0';
      wait for 30 ns; -- s_0
      tb_sa <= '1';
      tb_sb <= '0';
      wait for 30 ns; -- s_3
      tb_sa <= '1';
      tb_sb <= '1';
      wait for 30 ns; -- s_2
      tb_sa <= '0';
      tb_sb <= '1';
      wait for 30 ns; -- s_1
      tb_sa <= '0';
      tb_sb <= '0';          
    end loop;

  end process;

end architecture;
